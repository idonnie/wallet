package wallet.client

import org.apache.log4j.Logger
import com.antonkulyk.wallet.conf.Def
import com.typesafe.config.ConfigFactory
import com.antonkulyk.wallet.protocol.Messages._
import ClientRoot.timeout
import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Terminated
import akka.dispatch.Await
import akka.pattern.ask
import com.antonkulyk.wallet.util.Str

object ClientRoot {
  
  private[this] val logger = Logger.getLogger(ClientRoot.getClass());  
    
  /**
   * Constants
   */
  implicit val timeout = Def.Wait.Http 
  
  /**
   * Environment
   */
  lazy val actorSystemConf = ConfigFactory.load(this.getClass.getClassLoader)
  lazy val actorSystem = ActorSystem("clientsys", actorSystemConf.getConfig("clientconf").withFallback(actorSystemConf))
  lazy val rootActor = actorSystem.actorOf(Props[ClientRoot], "root")
  
  def start(userName: String) = {
	  val msg = StartClient(userName)
	  Await.result(rootActor ? msg, timeout.duration).asInstanceOf[ClientStarted] match {
	    case succ @ ClientStarted(userName) => 
		  // logger.info("[C][SUCC] Client started: " + succ)
	    case _ => 
	      logger.error("Terminated on waiting for response from " + userName + " actor")
	      throw new RuntimeException("[C][FAIL] Could not start client: " + msg)
	  }
  }

  def stop(userName: String) = {   
    val msg = StopClient(userName)
	Await.result(rootActor ? msg, timeout.duration).asInstanceOf[ClientStopped] match {
	  case succ @ ClientStopped(userName) => 
        logger.info("[C][SUCC] Client stopped: " + succ)
	  case msg => throw new RuntimeException("[C][FAIL] Could not stop client: " + msg)
	} 
  }  
  
}

class ClientRoot extends Actor with ActorLogging {
  import ClientRoot._
  
  def receive = {
    
    case start @ StartClient(userName) =>
      val client = context.actorOf(Props[Client], userName)
      context.watch(client)
      Await.result(client ? start, timeout.duration).asInstanceOf[ClientStarted] match {
        case started @ ClientStarted(userName) => 
          sender ! started
        case _ => 
          throw new RuntimeException("[C][FAIL] Could not start client: " + start)
      }  
          
    case StopClient(userName) => 
      context.actorFor(userName) ! GracefullyStopClient(userName, sender)
      
    case Terminated(client) =>
      log.debug("[C] Client actor has terminated: " + client)
 
    case msg @ _ =>
      log.error("[C][FAIL] Not supported message: " + msg)
      
  }
   
}
