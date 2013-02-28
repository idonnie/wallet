package wallet.server

import java.util.concurrent.atomic.AtomicLong

import scala.math.BigDecimal.int2bigDecimal
import scala.math.BigDecimal.long2bigDecimal
import scala.math.BigDecimal.javaBigDecimal2bigDecimal

import com.antonkulyk.wallet.conf.Def
import com.antonkulyk.wallet.model.BalanceUpdate
import com.antonkulyk.wallet.protocol.Messages.Client2Server
import com.antonkulyk.wallet.protocol.Messages.GracefullyStopServer
import com.antonkulyk.wallet.protocol.Messages.Server2Client
import com.antonkulyk.wallet.protocol.Messages.ServerGatherStatisticsTick
import com.antonkulyk.wallet.protocol.Messages.ServerStarted
import com.antonkulyk.wallet.protocol.Messages.ServerStopped
import com.antonkulyk.wallet.protocol.Messages.StartServer
import com.antonkulyk.wallet.util.DateTime

import Server.StatisticsGatherPeriod
import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.dispatch.Future
import akka.util.duration.intToDurationInt


object Server {
  
  val StatisticsGatherPeriod = 1 minutes
  
}

class Server extends Actor with ActorLogging {
  import Server._
  
  /**
   * We could also use this custom executor for Db tasks - 
   * somehow inconveniently
   */
/* 
  val MaxDbThreads = scala.math.max(Runtime.getRuntime().availableProcessors() * 4, 16)
  implicit val executor: ExecutionContext = ExecutionContext.fromExecutorService(new BlockingThreadPoolExecutor(
    MaxDbThreads, MaxDbThreads, 0L, TimeUnit.MILLISECONDS
    , new LinkedBlockingQueue[Runnable](), MaxDbThreads, "Db thread"
  ))
*/    
  
  
  val state: ServerPersistance = new ServerPersistanceDb()  
  val stats: ServerStats = new ServerStats()
  
  /**
   * Modifiable server state
   */
  trait ServerPersistance {
    def consumeTran(userName: String, tranId: Long, balDelta: BigDecimal, who: ActorRef): BalanceUpdate
  }
  
  /**
   * Hibernate persistance
   */
  class ServerPersistanceDb extends ServerPersistance {
    
    override def consumeTran(userName: String, tranId: Long, balDelta: BigDecimal, who: ActorRef) = {
      BalanceUpdate.balanceUpdate(userName, balDelta.underlying())
    }
    
  }  
  
  /**
   * Alternative in-memory persistance
   */
  class ServerPersistanceMem extends ServerPersistance {
    
    /**
     * Format: (userName, balVer) -> bal
     */
    @volatile var players = Map[(String, Long), BigDecimal]()

    override def consumeTran(userName: String, tranId: Long, balDelta: BigDecimal, who: ActorRef): BalanceUpdate = {
      
      // find players record with max balVer
      val rec = players.filter(el => el._1._1 == userName).foldLeft((userName, 0L))(
        (acc: (String, Long), el) => if (el._1._2 >= acc._2) el._1 else acc
      )
      val recBalance = players.getOrElse(rec, BigDecimal(0))
      val newRec = Tuple2(userName, rec._2 + 1) -> (recBalance + balDelta)
      
      return if (newRec._2 >= 0) {
        players += newRec
        new BalanceUpdate(
          userName, rec._2, recBalance.underlying()
          ,userName, newRec._1._2, newRec._2.underlying()
        )
      } else {
        
        /*
     	 * Client will be notified:
		 * next.balVer lasts the same
		 * next.bal NEGATIVE
		 */        
         new BalanceUpdate(
           userName, rec._2, recBalance.underlying()
           ,userName, rec._2, newRec._2.underlying()
         )       
       }     
    }      
  }
    
  
  /**
   * Modifiable server statistics
   */
  class ServerStats {
    
    @volatile var queries: Long = 0
    @volatile var queryMinTime: Long = Long.MaxValue
    @volatile var queryMaxTime: Long = 0    
    @volatile var queryAvgTime: Long = 0    
    
    @volatile var stats = List[(Long, Long, String)]()
    
    @volatile private[this] var currTimestamp: AtomicLong = new AtomicLong(System.currentTimeMillis())
    
	/**
	 * Unique time stamp - will also work in a non-actor environment also
	 */
	def createTimestamp(): Long = {
	  var timestamp: Long = 0
	  var currTs: Long = 0
	  do {	    	
	    timestamp = System.currentTimeMillis()
	    currTs = currTimestamp.get()
	    if ((timestamp | currTs) < 0) {
	      throw new UnsupportedOperationException
	        "Negative timestamps - should not happen: " + "timestamp=" + timestamp + ",currTs=" + currTs	      
	    }
	    
	    // unique
	    if (currTs >= timestamp) timestamp = currTs + 1
	  } while (
	    timestamp == 0
	    || timestamp == Long.MaxValue	      
	    || (!currTimestamp.compareAndSet(currTs, timestamp))
	  )    
	  return timestamp;
	}    
    
    def startQuery() = {
      val ts = createTimestamp()
      stats = (ts, ts, "[START]") :: stats 
      ts
    }
    
    def finishQuery(startTs: Long) = {
      val ts = createTimestamp()
      stats = (ts, startTs, "[FINAL]") :: stats 
      ts
    }
    
    def update() = {
      val gatheredStats = stats.foldLeft((0L, BigDecimal(0), 0L, 0L))(
        (acc: (Long, BigDecimal, Long, Long), el: (Long, Long, String)) => {
          if (el._3.indexOf("[FINAL]") >= 0) {
            val dt = el._1 - el._2
            (acc._1 + 1
              ,acc._2 + dt
              ,if (dt >= acc._3) acc._3 else dt
              ,if (dt <= acc._4) acc._4 else dt
            )
          } else acc
        }
      )
      stats = List.empty
      queries = gatheredStats._1
      queryMinTime = gatheredStats._3
      queryMaxTime = gatheredStats._4
      queryAvgTime = (gatheredStats._2 / BigDecimal(queries)).toLong
    }
    
    override def toString() = 
      "(queries=" + queries + 
      ",queryMinTime=" + DateTime.timeDistance(0, queryMinTime * Def.NANOS) +
      ",queryMaxTime=" + DateTime.timeDistance(0, queryMaxTime * Def.NANOS) +
      ",queryAvgTime=" + DateTime.timeDistance(0, queryAvgTime * Def.NANOS) + ")"
  }
  
  
  def schedule(who: ActorRef) = 
    ServerRoot.actorSystem.scheduler
      .scheduleOnce(StatisticsGatherPeriod)(who ! ServerGatherStatisticsTick)        
  
  def receive = {
    
    case StartServer(nodeName) => 
      schedule(self)
      sender ! ServerStarted(nodeName)
      
    case ServerGatherStatisticsTick =>
      stats.update
      log.warning("[S][STATS] " + stats.toString())
      schedule(self)      
      
    case msg @ Client2Server(userName, tranId, balDelta) => 
      val ts = stats.startQuery()
      implicit val executor = context.dispatcher
      
      val f = Future[BalanceUpdate] { 
        
        // Asynchroniously update player's wallet using
        // either i (val state is ServerStateMem) 
        // or Hibernate through GORM (val state is ServerStateDb)
        state.consumeTran(userName, tranId, balDelta, sender)
        
      } andThen { 
        case either if either.isLeft => 
          log.error("[S][FAIL] Player balance update failed: " + either.left.get)
          sender ! Server2Client(tranId, -1, -1, 0, 0)
          stats.finishQuery(ts);        
          null
        case either if either.isRight =>
          val balanceUpdate = either.right.get
          if (BigDecimal(balanceUpdate.getToBal()) >= 0) {
            log.info(
              (if (balDelta >= 0) "[IN]" else "[OUT]") + 
              "[" + balDelta + "]" +
              "[" + userName + "] " + 
              "[S][SUCC] Player state change: " + balanceUpdate
            )
            
            // Notify client
            sender ! Server2Client(tranId, 0, balanceUpdate.getToBalVer, balDelta, balanceUpdate.getToBal) 
          } else {
            log.error("[S][FAIL] Player consume transaction: " + balanceUpdate)  
            
            // Notify client
            sender ! Server2Client(tranId, -1, balanceUpdate.getFromBalVer, 0, balanceUpdate.getFromBal)        
          }
          stats.finishQuery(ts);
          balanceUpdate
      }
      
    case GracefullyStopServer(nodeName, askTo) =>
      askTo ! ServerStopped(nodeName)
      context.stop(self)

    case msg @ _ =>
      log.error("[S][FAIL] Not supported message: " + msg)
      
  }
   
}
