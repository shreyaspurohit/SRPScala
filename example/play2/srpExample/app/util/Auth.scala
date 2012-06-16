package util
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import controllers.Application
import org.apache.commons.ssl.OpenSSL
import java.nio.charset.Charset
import play.api.templates.Html

/**
 * This class provides the required authentication check to allow a request to
 * pass through. This uses the ExampleSRPServer to authenticate. 
 */
object Auth {
  val UTF8 = Charset.forName("UTF-8")
  val OPENSSL_ALGO = "aes-128-cbc"
  
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
  
  def EncryptedAuthenticatedPage(username:String, sessionId:String, page:String)(f :Tuple5[String,String,String,Request[AnyContent], String => String] => Result) = {
     import srp._
     import com.bitourea.srp.common.Util.hexBytesWrapper
	 Action { request =>
	    val (s,h) = ExampleSRPServer.getSessionWithHash(username)
	    val key = BigInt(h, 16).toByteArray.toHexString //Must sync with UI hexstring. (Beginning 0 and format)
	    println(">>> key: " + BigInt(h, 16).toByteArray.toHexString)
	    val sessionIdDec = OpenSSL.decrypt(OPENSSL_ALGO, key.toCharArray(), sessionId.getBytes(UTF8));
	    if(ExampleSRPServer.validSession(username, new String(sessionIdDec, UTF8))){
	      val pageDec = OpenSSL.decrypt(OPENSSL_ALGO, key.toCharArray(), page.getBytes(UTF8));
	      val fEnc = {data:String =>
	        new String(OpenSSL.encrypt(OPENSSL_ALGO, key.toCharArray(), data.getBytes(UTF8)), UTF8).replaceAll("\n", "").replaceAll("\r","");
	      }
	      val arguments = (username, new String(sessionIdDec, UTF8), new String(pageDec, UTF8), request, fEnc)
	      println(">>>: " + username + ", " + new String(sessionIdDec, UTF8) + ", " + new String(pageDec, UTF8))
	      f(arguments)
	    }	      
	    else
	      controllers.Application.Ok("Error: Authentication Failure")
	 }
  }
}