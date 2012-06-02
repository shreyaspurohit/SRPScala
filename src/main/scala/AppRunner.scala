import java.security.SecureRandom
/**
 * This class runs the server and the client side scala code, displaying the generated/calculated
 * SRP parameters and validations.
 */
object AppRunner {
	def main(args:Array[String]){
	  import com.bitourea.srp.common.Util._
	  import com.bitourea.srp.common._
	  
	  object srp extends SRPParameter
	  object srpserver extends ServerSRPParameter
	  object srpclient extends ClientSRPParameter
	  
	  val sr = new SecureRandom
	  var abytes:Array[Byte] = new Array(32)
	  var bbytes:Array[Byte] = new Array(32)
      
	  //Commons
	  println("Commons")
	  println("-------")
	  val nhex = srp.N.toByteArray.toHexString
	  println("N: " + nhex)
	  val ghex = Array(srp.g.toByte).toHexString
	  println("g: " + ghex)
	  println("k: " + srp.k.toHexString)
	  println()
	  
	  //Server side
	  println("Server")
	  println("------")
	  //Gets the username
	  val I = "user"
//	  The host stores passwords using the following formula:
//  x = H(s, p)               (s is chosen randomly)
//  v = g^x                   (computes password verifier)
	  val (sVal,xVal) =  srpserver.x("password")
	  val vVal = srpserver.v(xVal)
	  println("x: " + xVal.toHexString)
	  println("s: " + sVal.toHexString)
	  println("v: " + vVal.toHexString)
	  println()
	  //Client Side
	  println("------")
	  
	  println("Client -> Server")
	  println("-----------------")
	  println("I: " + I)	  
	  val a = srpclient.a
	  println("a: " + a.toHexString)
	  val A = srpclient.A(a)
	  println("A: " + A.toHexString)
	  println()
	  
	  println("Server -> Client")
	  println("-----------------")
	  val b = srpserver.b
	  val B = srpserver.B(vVal, b)
	  println("s: " + sVal.toHexString) 
	  println("B: " + B.toHexString)
	  println()
	  
	  println("Client/Server computes u")
	  println("------------------------")
	  val u = H(A,B)
	  println("u: " + u.toHexString)
	  println()
	  
	  println("Client computes")
	  println("----------------")
	  val x = srpclient.x(sVal, "password".getBytes())
	  println("x: " + x.toHexString)
	  val S = srpclient.S(BigInt(x), BigInt(B), BigInt(a), BigInt(u))
	  println("S: " + S.toHexString)
	  val K = H(S)
	  println("K: " + K.toHexString)
	  println()
	  
	  println("Server computes")
	  println("---------------")
	  val Sserver = srpserver.S(A,vVal,u,b)
	  println("S: " + Sserver.toHexString)
	  val Kserver = H(Sserver)
	  println("K: " + Kserver.toHexString)
	  
	  println()
	}
}
