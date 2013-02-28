package wallet.log;

import java.util.Date;

import org.apache.log4j.Logger;


/**
* Absolutely dumb and robust logger. No imports
*/

public final class NubLogger {
	
    private static final Logger logger = Logger.getLogger(NubLogger.class);
    
//	private static volatile Object logger = null;
	
	public static String SYSTEM_OUT = "System.out";
	public static String SYSTEM_ERR = "System.err";
	public static String LOG4J_ANY = "";		
	public static String NULL = null;		
	
    public static final int THROWABLE_MAX_ROWS = 65535; // making it less than 8, is sometimes inconvenient

    public NubLogger() {  
    	
    }

    public static String log(Object msg) {
        return staticLog(LOG4J_ANY, msg);
    }
    
    public static String toStringWithTimestamp(Object o) {
        return new Date().toString() + ": " + toString(o);
    }    
        
    public static String toString(Object o) {
        return staticLog(NULL, o);
    }

    public static String log(String logName, Object o) {
        return staticLog(logName, o);    
    }
    
    public static void handle(Throwable t) {
    	handle(SYSTEM_ERR, t);
    }
    
    public static void handle(Runnable r) {
    	try {
    		r.run();
    	} catch (Throwable t) {
    		handle(SYSTEM_ERR, t);    	}
    	
    }    
    
    public static void handle(String logName, Throwable t) {
        if (t == null) {  
            t = new Throwable(NubLogger.class.getName() + ".handle(logName, (Throwable) null)");            
        }
        if (t instanceof RuntimeException) {
        	log(logName, t);
        	throw (RuntimeException) t;
        } else if (t instanceof Exception) {
        	log(logName, t);
        	throw new RuntimeException(t);
        } else if (t instanceof Error) {
        	log(logName, t);
        	throw (Error) t;
        } else if (t instanceof Throwable) {
        	log(logName, t);
        	throw new RuntimeException(t);
        } else {
        	String msg = "Unknown throwable type: " + t.getClass(); 
        	log(logName, msg);
        	throw new UnsupportedOperationException(msg);
        }
    }

    static String staticLog(String logName, Object o) {
        final String thisClass = NubLogger.class.getName();
        if (o == null) {  
            o = new Throwable(thisClass + ".staticLog(logName, (Object) null)");            
        }
        if (o instanceof Throwable) {            
            final Throwable t = (Throwable) o;
            int rows = THROWABLE_MAX_ROWS;
            String msg = "";
            {
                String s = t.getMessage();
                if (s == null || s.trim().length() == 0) {
                    s = "\"\"";
                }
                s = t.getClass().getName() + ": " + s;
                
                if (cmp(logName, SYSTEM_OUT)) {
                	System.out.println(s);
                } else if (cmp(logName, SYSTEM_ERR)) {
                	System.err.println(s);
                } else if (cmp(logName, NULL)) {
                	// nothing
                } else {
                	// logger.error(s);
                	// org.apache.log4j.Logger.getLogger(NubLogger.class).error(s);
                	error(s);
                }             
                                
                msg += s;
                msg += "\n";
                --rows;
            }
            StackTraceElement[] stackTrace = t.getStackTrace();
            if (stackTrace == null) {
                {
                    String s = ">> (no stack trace)";
                    
                    if (cmp(logName, SYSTEM_OUT)) {
                    	System.out.println(s);
                    } else if (cmp(logName, SYSTEM_ERR)) {
                    	System.err.println(s);
                    } else if (cmp(logName, NULL)) {
                    	// nothing
                    } else {
                    	// logger.error(s);
                    	// org.apache.log4j.Logger.getLogger(NubLogger.class).error(s);
                    	error(s);
                    }
                    
                    msg += s;
                    msg += "\n";
                    --rows;
                }                
            } else {
                for (int i = 0; i < stackTrace.length && rows > 0; ++i) {
                    if (stackTrace[i] == null) {                            
                        continue;
                    }
                    String s = ">> " + stackTrace[i].toString();
                    
                    if (cmp(logName, SYSTEM_OUT)) {
                    	System.out.println(s);
                    } else if (cmp(logName, SYSTEM_ERR)) {
                    	System.err.println(s);
                    } else if (cmp(logName, NULL)) {
                    	// nothing
                    } else {
                    	// logger.error(s);
                    	// org.apache.log4j.Logger.getLogger(NubLogger.class).error(s);
                    	error(s);
                    }             
                    
                    msg += s;
                    msg += "\n";                    
                    --rows;
                }
            }
            rows = THROWABLE_MAX_ROWS;
            Throwable c = t.getCause();
            if (c != null) {
                ++rows;
                {
                    String s = c.getMessage();
                    if (s == null || s.trim().length() == 0) {
                        s = "\"\"";
                    }
                    s = ">> Cause: " + c.getClass().getName() + ": " + s;
                    
                    if (cmp(logName, SYSTEM_OUT)) {
                    	System.out.println(s);
                    } else if (cmp(logName, SYSTEM_ERR)) {
                    	System.err.println(s);
                    } else if (cmp(logName, NULL)) {
                    	// nothing
                    } else {
                    	// logger.error(s);
                    	// org.apache.log4j.Logger.getLogger(NubLogger.class).error(s);
                    	error(s);
                    }             
                    
                    msg += s;
                    msg += "\n";                    
                    --rows;
                }
                stackTrace = c.getStackTrace();
                if (stackTrace == null) {
                    String s = ">> (no stack trace)";
                    
                    if (cmp(logName, SYSTEM_OUT)) {
                    	System.out.println(s);
                    } else if (cmp(logName, SYSTEM_ERR)) {
                    	System.err.println(s);
                    } else if (cmp(logName, NULL)) {
                    	// nothing
                    } else {
                    	// logger.error(s);
                    	// org.apache.log4j.Logger.getLogger(NubLogger.class).error(s);
                    	error(s);
                    }             
                    
                    msg += s;
                    msg += "\n";                    
                    --rows;
                } else {
                    for (int i = 0; i < stackTrace.length && rows > 0; ++i) {
                        if (stackTrace[i] == null) {                            
                            continue;
                        }
                        String s = ">> " + stackTrace[i].toString();
                        
                        if (cmp(logName, SYSTEM_OUT)) {
                        	System.out.println(s);
                        } else if (cmp(logName, SYSTEM_ERR)) {
                        	System.err.println(s);
                        } else if (cmp(logName, NULL)) {
                        	// nothing
                        } else {
                        	// logger.error(s);
                        	error(s);
                        	// org.apache.log4j.Logger.getLogger(NubLogger.class).error(s);
                        }             
                        
                        msg += s;
                        msg += "\n";                        
                        --rows;
                    }                        
                }
            }
            return removeTrailing(msg, "\n");                        
        }        
        String s = null;
        try {
            s = o.toString();
        } catch (StackOverflowError e) {
            log(e);
        } catch (ThreadDeath t) {
            throw (ThreadDeath) t;                
        } catch (Throwable t) {
            log(t);
            if (t instanceof Error) {
                throw (Error) t;
            }
        }
        if (s == null) {
            s = o.getClass().getName() + ".toString()==NuLL"; 
        } else if (s.trim().length() <= 0) {
            s = "\"\""; 
        }
        s = (logName != null ? logName + " " : "") + s;
        
        if (cmp(logName, SYSTEM_OUT)) {
        	System.out.println(s);
        } else if (cmp(logName, SYSTEM_ERR)) {
        	System.err.println(s);
        } else if (cmp(logName, NULL)) {
        	// nothing
        } else {
        	// logger.error(s);
        	// org.apache.log4j.Logger.getLogger(NubLogger.class).error(s);
        	error(s);
        }             
        
        return s;        
    }

    /**
     * Removes trailing string, for example <code>removeTrailing("/opt/Promin/Plugins/", "/")</code>
     */
    static String removeTrailing(String s, String trailing) {
        if (s == null || trailing == null || s.length() < trailing.length()) {
            return s;
        }
        return s.endsWith(trailing) ? s.substring(0, s.length() - trailing.length()) : s;        

    }      
    
    /**
     * Compares 2 Strings, also null-s 
     */
    static boolean cmp(String s0, String s1) {
    	return s0 == s1 || (s0 != null && s0.equals(s1)); 
    }
    
    static void error(String s) {
    	logger.error(s);
    } 

}