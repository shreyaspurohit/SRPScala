package util
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import controllers.Application

/**
 * This class provides the required authentication check to allow a request to
 * pass through. This uses the ExampleSRPServer to authenticate. 
 */
object Auth {
  case class AuthenticatedRequest(
		  val username: String, sessionId: String, request: Request[AnyContent]
  ) extends WrappedRequest(request)

  def Authenticated(username:String, sessionId:String)(f: AuthenticatedRequest => Result) = {
     import srp._     
	 Action { request =>
	    if(ExampleSRPServer.validSession(username, sessionId))
	      f(AuthenticatedRequest(username, sessionId, request))
	    else
	      controllers.Application.Ok("Error: Authentication Failure")
	 }
  }
}