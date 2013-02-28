package wallet.client

import scala.Option.option2Iterable
import scala.collection.SortedSet
import scala.math.BigDecimal.int2bigDecimal
import scala.util.Random

import com.antonkulyk.wallet.protocol.Messages._

import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.util.duration.intToDurationInt

object Client {
  
  val BalanceChangePeriod = 5 seconds
  val MaxAmountInCents = 10000
  
  /*
   * TODO Move to application.conf
   */
  val WalletLocation = "akka://serversys@127.0.0.1:2553/user/root/wallet"
    
}

class Client extends Actor with ActorLogging {
  import Client._
  
  val state: ClientState = new ClientState()
  
  /**
   * Modifiable client state
   */
  class ClientState {
    
    @volatile var userName: Option[String] = None
    @volatile var bal: BigDecimal = 0
    @volatile var balVer: Long = 0   
    @volatile var tranId: Option[Long] = None    
    
    /**
     * All money transactions sent by client are stored here
     * Format: (tranId, balVer)
     */
    @volatile var sentTran = SortedSet[(Long, Long)]()
    
    /**
     * Unique, incrementing
     */
    def nextTranId: Long = {      
      val result = System.currentTimeMillis() 
      if (sentTran.size > 0 && sentTran.last == result) nextTranId
      else {
        sentTran = sentTran + Tuple2(result, balVer)
        result
      }
    } 

    def consumeTran(tranId: Long, errCode: Int, balVer: Long, balDelta: BigDecimal, balAfter: BigDecimal): Unit = {
      if (errCode >= 0) {
        val tranRecordOpt = sentTran.find(el => el._1 == tranId).headOption
        if (tranRecordOpt.isDefined) {
          val tranRecord = tranRecordOpt.get
          sentTran = sentTran.filter(el => el._1 > tranRecord._1)
          bal = balAfter
          this.balVer = balVer
          this.tranId = Some(tranId)
          log.info("[C][SUCC] State change: " + toString)
          return
        }  
      }  
      log.error("[C][FAIL] Consume transaction: " + 
        toString + " => " +
        "(tranId=" + tranId + 
        ",errCode=" + errCode +
        ",balVer=" + balVer +
        ",balDelta=" + balDelta + 
        ",balAfter=" + balAfter + ")"
      ) 
    }
    
    override def toString = 
      "(userName=" + userName +
      ",tranId=" + (if (!tranId.isDefined) "None" else tranId.get) + 
      ",balVer=" + balVer +
      ",bal=" + bal + ")"
    
  }  
  
  def schedule(who: ActorRef) = 
    ClientRoot.actorSystem.scheduler
      .scheduleOnce(BalanceChangePeriod)(who ! ClientGenerateAmountTick)      
      
  def receive = {
    
    case StartClient(userName) =>
      state.userName = Some(userName)
      schedule(self)
      sender ! ClientStarted(userName)
      
    case ClientGenerateAmountTick =>
        val tranId = state.nextTranId
        Random.setSeed(tranId)
        val (digits, zero, dot) = (Random.nextInt(MaxAmountInCents).toString(), "0", ".")
        
        /*
         * Probability 2/5 of negative amount
         * Probability 1/20 of a Jackpot
         */
        val randomAmount =  
          if (1 > Random.nextInt(20)) {
            "-999999.99"
          } else {
            (if (2 > Random.nextInt(5)) "-" else "") +
            (if (digits.length - 2 <= 0) zero + dot + digits
            else digits.take(digits.length - 2) + dot + digits.drop(digits.length - 2))
          }
        val server = ClientRoot.actorSystem.actorFor(WalletLocation)
        server ! Client2Server(state.userName.get, tranId, BigDecimal(randomAmount))
        schedule(self)
      
    case msg @ Server2Client(tranId, errCode, balVer, balDelta, balAfter) =>
      state.consumeTran(tranId, errCode, balVer, balDelta, balAfter)
   
    case GracefullyStopClient(userName, askTo) =>
      askTo ! ClientStopped(userName)
      context.stop(self)

    case msg @ _ =>
      log.error("[C][FAIL] Not supported message: " + msg)
      
  }
   
}
