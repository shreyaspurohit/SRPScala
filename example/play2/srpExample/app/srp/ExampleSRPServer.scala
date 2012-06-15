package srp
import com.bitourea.srp.common.SRPServer
import java.io._
case class UserData(userName:String, s:Array[Byte], v:Array[Byte])
/**
 * Need this class to update the saved data file. 
 */
class AppendableObjectOutputStream(out:OutputStream) extends ObjectOutputStream(out) {
  
  @Override
  override def writeStreamHeader():Unit = {
    // do not write a header
  }
  
}

/**
 * The class provides a sample implementation of SRPServer where the data is saved in file,
 * and the session details are hold in memory.
 */
object ExampleSRPServer extends SRPServer{

  var cache:Map[String, UserData] = Map() 
  /**
   * Saves the user credentials provided in file.
   */
  override def save(userName:String, s:Array[Byte], v:Array[Byte]):Unit = {
    val file = new File("user.data");
    def fileOs = new FileOutputStream(file.getName(),true);
    if(!hasUser(userName)){
	  var out:ObjectOutputStream = null
      if(file.exists()){
        out = new AppendableObjectOutputStream(fileOs);
      }else{
        out = new ObjectOutputStream(fileOs);
      }
	  out.writeObject(UserData(userName,s,v))
	  out.flush();
      out.close();
    }
    
  }
  
  /**
   * Finder method to get the s and v for the given user with userName.
   * This implementation reads the serialized objects from the file.
   */
  override def findSV(userName:String):Option[Tuple2[Array[Byte], Array[Byte]]] = {
	cache.get(userName).orElse[UserData]{
	  val file = new File("user.data");
	  if(file.exists()){
		  val fileIs = new FileInputStream(file.getName());
		  val in = new ObjectInputStream(fileIs);
		  var ud:Object = null
		  var flag:Boolean = false
		  var result:Boolean = false
		  while(flag == false){
		    try{
		    	ud = in.readObject();
			    if(ud.asInstanceOf[UserData].userName == userName){
				  cache += (ud.asInstanceOf[UserData].userName -> ud.asInstanceOf[UserData])
				  result = true
				  flag = true
			    }
		    }catch{
		      case e:EOFException =>
		        flag=true
		    }
		  }
		  in.close();
		  if(result)Some(ud.asInstanceOf[UserData]) else None
	  }else{
	      None
	  }
	}.map(data => Some(data.s, data.v)).getOrElse(None)	
  }
  
  def hasUser(userName:String) = {
      import scala.util.control.Exception._
	  catching(classOf[FileNotFoundException],classOf[EOFException]).opt(findSV(userName).isDefined).getOrElse(false)
  }
  
  /**
   * Provides a way to save sessionid and the associated username.
   */
  var currentSession:Map[String,(String,String)] = Map()
  def saveSession(userName:String, sessionId:String, hsessionId:String):Unit = {
    currentSession += (userName -> (sessionId,hsessionId))
  }
  
  def validSession(userName:String, inSessionId:String):Boolean = {
    val (s,h) = currentSession.getOrElse(userName, ("",""))
    s.equals(inSessionId)
  }
  
  def validateSessionHash(userName:String, inhSessionId:String):Boolean = {
    val (s,h) = currentSession.getOrElse(userName, ("",""))
    BigInt(h,16).equals(BigInt(inhSessionId,16))
  }
  
  def getSession(userName:String) = {
    val (s,h) = currentSession.getOrElse(userName, ("",""))
    s
  }
  
  def getSessionWithHash(userName:String) = {
    val (s,h) = currentSession.getOrElse(userName, ("",""))
    (s,h)
  }  
}