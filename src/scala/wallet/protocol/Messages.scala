package wallet.protocol
import akka.actor.ActorRef

object Messages {
  
  /**
   * Protocol messages
   */
  case class StartClient(userName: String) 
  case class ClientStarted(userName: String) 
  case class StopClient(userName: String) 
  case class GracefullyStopClient(userName: String, askTo: ActorRef) 
  case class ClientStopped(userName: String) 
  case class ClientGenerateAmountTick()

  case class StartServer(nodeName: String) 
  case class ServerStarted(nodeName: String) 
  case class StopServer(nodeName: String) 
  case class GracefullyStopServer(nodeName: String, askTo: ActorRef) 
  case class ServerStopped(nodeName: String)   
  case class ServerGatherStatisticsTick()
  
  case class Client2Server(userName: String, tranId: Long, balDelta: BigDecimal)
  case class Server2Client(tranId: Long, errCode: Int, balVer: Long, balDelta: BigDecimal, balAfter: BigDecimal)    

}