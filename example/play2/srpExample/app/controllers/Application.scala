package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import util.Auth._ 

case class Register(username:String, password:String)

object Application extends Controller {
  
  val registerForm = Form(
	  mapping(
	    "Username" -> nonEmptyText,
	    "Password" -> nonEmptyText
	  )(Register.apply)(Register.unapply)
  )
  
  /**
   * @param viewType Based on the viewType static pages are displayed. Available values
   * 				 are "index", "register", and "login"
   */
  def index(viewType:String) = Action {
    viewType match {
      case "index" =>
        Ok(views.html.index())
      case "register" =>
        Ok(views.html.register(registerForm)(List()))
      case "login" =>
        Ok(views.html.login())
    }    
  }
  
  /**
   * Called from the JS on login.scala.html. Used to login and establish a session.
   */
  def login = Action(implicit request =>{
    import srp._
      println("Login at server: " + request.body.asFormUrlEncoded)
      val params = request.body.asFormUrlEncoded
      if(params.isEmpty ||
          !params.get.keySet.contains("A") ||
          !params.get.keySet.contains("username")){
        Ok("Error: Require A and username parameters")
      }else{
    	 val Aval = params.get("A")
		 val username = params.get("username")
		 
		 if(Aval.size == 0 || username.size == 0 || Aval(0).size == 0){
			 Ok("Error: Require nonempty A and username parameters")
		 }else{
		   //Required parameters from the client- A and username
			 val result = ExampleSRPServer.getSessionWithClientParameters(username(0), Aval(0))
			 if(result.isDefined){
				 val (sessionId, hSessionId, s, bvalStr) = result.get
				 ExampleSRPServer.saveSession(username(0), sessionId, hSessionId)
				 println(">>> session id: " + sessionId)
				 Ok(s + "," + bvalStr)//Return the salt s and calculated value B
			 }else{
				 Ok("Error: Authentication Failure")
			 }			   
		 }
      }
  }
  )
  
  /**
   * Used by register.scala.html. The input username and password are used to save 
   * the credentials by ExampleSRPServer.
   *  
   */
  def register = Action(implicit request => {
      import play.api.templates.Html
      import srp._
      
      val errorsHandler = (formWithErrors:Form[Register]) => 
    	      Ok(views.html.register(registerForm)(List("Invalid password")))
      
      val successHandler = (register:Register) => {
        if(ExampleSRPServer.hasUser(register.username)){
            Ok(views.html.register(registerForm)(List("User Exists")))
        }else{
        	ExampleSRPServer.saveUserCredentials(register.username,register.password)
    	  	Ok(views.html.main("SRP Example")(Html("""
	      <p><a href="/">Back</a></p>
		    <label> Registration Successful </label>
	      """)))
        }
      }
	  registerForm.bindFromRequest.fold(errorsHandler,successHandler)
     }
  )
  
  /**
   * The secret page that needs authentication to be seen.
   */
  def secret(username:String, sessionId:String) = Authenticated(username, sessionId){request =>
												     Ok(views.html.secret())
 											      } 

  /**
   * @param username The username
   * @param K The SRP parameter K which is H(sessionId)
   * @return Action With SessionId or Error string 
   */
  def validateServer(username:String, K:String) = Action(implicit request =>{
    import srp._
    if(ExampleSRPServer.validateSessionHash(username, K)){
    	Ok(ExampleSRPServer.getSession(username))
    }else{
        Ok("Error: Authentication Failure. K mismatch.")
    }
    
  })
  
}