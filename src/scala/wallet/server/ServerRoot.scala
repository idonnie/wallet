package wallet.server

import org.apache.log4j.Logger

import com.antonkulyk.wallet.conf.Def
import com.typesafe.config.ConfigFactory
import com.antonkulyk.wallet.protocol.Messages._

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Terminated
import akka.actor.actorRef2Scala
import akka.dispatch.Await
import akka.pattern.ask

object ServerRoot {
  
  private[this] val logger = Logger.getLogger(ServerRoot.getClass());  
  
  
  /**
   * Constants
   */
  implicit val timeout = Def.Wait.Http 
  
  /**
   * Environment
   */
  lazy val actorSystemConf = ConfigFactory.load(this.getClass.getClassLoader)
  lazy val actorSystem = ActorSystem("serversys", actorSystemConf.getConfig("serverconf").withFallback(actorSystemConf))
  lazy val rootActor = actorSystem.actorOf(Props[ServerRoot], "root")
  
  def start(): Unit = start("wallet")
  def stop(): Unit = stop("wallet")
  
  def start(nodeName: String) = {
    val msg = StartServer(nodeName)
    Await.result(rootActor ? msg, timeout.duration).asInstanceOf[ServerStarted] match {
      case succ @ ServerStarted(nodeName) => 
        logger.info("[S][SUCC] Server started: " + succ)
      case _ => throw new RuntimeException("[S][FAIL] Could not start server: " + msg)
    }
  }

  def stop(nodeName: String) = {   
    val msg = StopServer(nodeName)
	Await.result(rootActor ? msg, timeout.duration).asInstanceOf[ServerStopped] match {
	  case succ @ ServerStopped(nodeName) => 
        logger.info("[S][SUCC] Server stopped: " + succ)
	  case msg => throw new RuntimeException("[S][FAIL] Could not stop server: " + msg)
	} 
  }  
  
}

class ServerRoot extends Actor with ActorLogging {
  import ServerRoot._
  
  def receive = {
    
    case start @ StartServer(nodeName) =>
      val server = context.actorOf(Props[Server], nodeName)
      context.watch(server)
      Await.result(server ? start, timeout.duration).asInstanceOf[ServerStarted] match {
        case started @ ServerStarted(nodeName) => 
          sender ! started
        case _ => 
          throw new RuntimeException("[S][FAIL] Could not start server: " + start)
      }  
          
    case StopServer(nodeName) => 
      context.actorFor(nodeName) ! GracefullyStopServer(nodeName, sender)
      
    case Terminated(server) =>
      log.debug("[S] Server actor has terminated: " + server)
 
    case msg @ _ =>
      log.error("[S][FAIL] Not supported message: " + msg)
      
  }
   
}
