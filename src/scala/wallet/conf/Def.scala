package wallet.conf;

import java.nio.charset.Charset;
import akka.util.Timeout
import akka.util.duration.intToDurationInt

/**
 * Global constants definition.
 */
object Def {
  
  /**
   * Timeout
   */
  object Wait {
    val Busy = Timeout(2 microseconds)
    val Tick = Timeout(1 millisecond)
    val Stop = Timeout(8 seconds)
    val Http = Timeout(20 seconds)
    val Long = Timeout(3 minutes)
  }
  
  /**
   *  Character set
   */
/*  
  object Charset {
    val Name = "UTF-8";
    val Charset = java.nio.charset.Charset.forName(Name);
  } 
*/  
  val CHARSET_NAME = "UTF-8"
  val CHARSET = java.nio.charset.Charset.forName(CHARSET_NAME)
  
  /**
    * Environment
	*/
  val LOGS_PATH = "/log";
  val CLASSES_PATH = "/WEB-INF/classes/";	
  val MONITOR_PROPERTIES_PATH = "/grails-app/conf/monitor/";
  val DB_PATH = "/db";	
	
  /**
   * Timeouts
   */
  val NANOS = 1000000; // (duration in millis) * NANOS = duration in nanos	
	
  val MILLIS_BUSY = 1; // minimal - for busy wait
  val MILLIS_TICK = 15; // tick duration for windows scheduler 
  val MILLIS_STOP = 8000; // termination time of some operation, usually expected by users  
  val MILLIS_HTTP = 20000; // usually expected completion time for remote operation
  val MILLIS_LONG = 180000; // usually expected completion time for long operation
	
  val NANOS_BUSY: Long = 2000; // minimal significant 
  val NANOS_TICK: Long = MILLIS_TICK * NANOS; // same
  val NANOS_TERMINATE: Long = MILLIS_STOP * NANOS; // same
  val NANOS_HTTP: Long = MILLIS_HTTP * NANOS; // same	
  val NANOS_LONG = MILLIS_LONG * NANOS; // same	

}
