package wallet.log;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

import wallet.util.Ref;


public class StacktraceLogger extends Logger {

	protected final Logger logger;

	protected StacktraceLogger(Logger logger) {
		super(logger.getName());
		this.logger = logger;
	}	

	/**
	 * Our fix to make Log4j and Grails, always log full stack trace
	 */
	@Override
	public void error(final Object message) {
		if (message != null && message instanceof Throwable) {
			logger.error("", (Throwable) message);
		} else {
			logger.error(message);
		}				
	}
	
	/**
	 * Our fix to make Log4j and Grails, always log full stack trace
	 */
	@Override
	public
	void info(final Object message) {
		if (message != null && message instanceof Throwable) {
			logger.info("", (Throwable) message);
		} else {
			logger.info(message);
		}
	}

	/**
	 * Our fix to make Log4j and Grails, always log full stack trace
	 */	
	@Override
	public void log(final Priority priority, final Object message) {
		if (message != null && message instanceof Throwable) {
			logger.log(priority, "", (Throwable) message);
		} else {
			logger.log(priority, message);
		}
	}

	/**
	 * Our fix to make Log4j and Grails, always log full stack trace
	 */		
	@Override
	public void warn(final Object message) {
		if (message != null && message instanceof Throwable) {
			logger.warn("", (Throwable) message);
		} else {
			logger.warn(message);
		}
	}	
	
	/**
	 * Our fix to make Log4j and Grails, always log full stack trace
	 */		
	@Override
	public void trace(final Object message) {		
		if (message != null && message instanceof Throwable) {
			logger.trace("", (Throwable) message);
		} else {
			logger.trace(message);
		}
	}	
	
	
	@Override
	public void debug(final Object message) {
		if (message != null && message instanceof Throwable) {
			logger.debug("", (Throwable) message);
		} else {
			logger.debug(message);
		}
	}

	@Override
	public
	void debug(final Object message, final Throwable t) {
		logger.debug(message, t);
	}	
	
	@Override
	public void error(final Object message, final Throwable t) {
		logger.error(message, t);
	}

	@Override
	public
	void fatal(Object message) {
		if (message != null && message instanceof Throwable) {
			logger.fatal("", (Throwable) message);
		} else {
			logger.fatal(message);
		}	
	}

	@Override
	public void fatal(Object message, Throwable t) {
		logger.fatal(message, t);		  
	}
	

	@Override
	public
	void info(Object message, Throwable t) {
		logger.info(message, t);
	}	

	@Override
	public
	void warn(Object message, Throwable t) {
		logger.warn(message, t);
	}	
	
	@Override
	public void trace(Object message, Throwable t) {
		logger.trace(message, t);
	}	
	
	@Override
	public void addAppender(Appender newAppender) {
		logger.addAppender(newAppender);
	}

	@Override
	public Enumeration getAllAppenders() {
		return logger.getAllAppenders();
	}

	@Override
	public Appender getAppender(String name) {
		return logger.getAppender(name);
	}

	@Override
	public boolean isAttached(Appender appender) {
		return logger.isAttached(appender);
	}

	@Override
	public void removeAllAppenders() {
		logger.removeAllAppenders();
	}

	@Override
	public void removeAppender(Appender appender) {
		logger.removeAppender(appender);
	}

	@Override
	public void removeAppender(String name) {
		logger.removeAppender(name);
	} 	

	@Override
	public void assertLog(final boolean assertion, final String msg) {
		logger.assertLog(assertion, msg);
	}

	@Override
	public void callAppenders(LoggingEvent event) {
		logger.callAppenders(event);
	}


	@Override
	protected
	void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
		Ref.callMethod("forcedLog", logger, new Class<?>[] {String.class, Priority.class, Object.class, Throwable.class}, new Object[] {fqcn, level, message, t});
	}

	@Override
	public
	boolean getAdditivity() {
		return logger.getAdditivity();
	}

	@Override
	public
	Level getEffectiveLevel() {
		return logger.getEffectiveLevel();
	}

	@Override
	public
	Priority getChainedPriority() {
		return logger.getChainedPriority();
	}
	
	@Override
	public LoggerRepository  getHierarchy() {
		return logger.getHierarchy();
	}

	@Override
	public
	LoggerRepository  getLoggerRepository() {
		return logger.getLoggerRepository();
	}

	@Override
	public
	ResourceBundle getResourceBundle() {
		return logger.getResourceBundle();
	}


	@Override
	protected String getResourceBundleString(String key) {			
		return Ref.callMethod("getResourceBundleString", logger, new Class<?>[]{String.class}, new Object[]{key});
	}


	@Override
	public
	boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public
	boolean isEnabledFor(Priority level) {
		return logger.isEnabledFor(level);
	}

	@Override
	public
	boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}


	@Override
	public
	void l7dlog(Priority priority, String key, Throwable t) {
		logger.l7dlog(priority, key, t);
	}

	@Override
	public
	void l7dlog(Priority priority, String key,  Object[] params, Throwable t) {
		logger.l7dlog(priority, key,  params, t);
	}

	@Override
	public
	void log(Priority priority, Object message, Throwable t) {
		logger.log(priority, message, t);
	}


	@Override
	public
	void log(String callerFQCN, Priority level, Object message, Throwable t) {
		logger.log(callerFQCN, level, message, t);
	}

	@Override
	public void setAdditivity(boolean additive) {
		logger.setAdditivity(additive);
	}
	@Override
	public void setLevel(Level level) {
		logger.setLevel(level);
	}

	@Override
	public void setPriority(Priority priority) {
		logger.setPriority(priority);
	}

	@Override
	public void setResourceBundle(ResourceBundle bundle) {
		logger.setResourceBundle(bundle);
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}	

	/*	  
	 *  // FIXME Override static?? Hope these methods are never called. 
	 *
	  public
	  static
	  void shutdown() {
	    LogManager.shutdown();
	  }
	  
	  static
	  public
	  Logger getLogger(String name) {
	    return LogManager.getLogger(name);
	  }

	  static
	  public
	  Logger getLogger(Class clazz) {
	    return LogManager.getLogger(clazz.getName());
	  }


	  public
	  static
	  Logger getRootLogger() {
	    return LogManager.getRootLogger();
	  }

	  public
	  static
	  Logger getLogger(String name, LoggerFactory factory) {
	    return LogManager.getLogger(name, factory);
	  }
	  
	  final
	  public
	  static
	  Category getRoot() {
	    return LogManager.getRootLogger();
	  }
	  
	  public
	  static
	  Category getInstance(String name) {
	    return LogManager.getLogger(name);
	  }

	  public
	  static
	  Category getInstance(Class clazz) {
	    return LogManager.getLogger(clazz);
	  }
	  
	  public
	  static
	  Enumeration getCurrentCategories() {
		return logger.getCurrentCategories();
	  }


	  public
	  static
	  LoggerRepository getDefaultHierarchy() {
	    return LogManager.getLoggerRepository();
	  }	  
	  
	public static
	Logger exists(String name) {
		Logger l = Logger.exists(name);
		if (l != null && l instanceof StacktraceLogger) {
			return l;
		} else {
			return new StacktraceLogger(l);
		}
	}	  
	  
	  
	 */

	
	/*
	 * // FIXME Override final?? Hope these methods are never called.
	 * 
	  final
	  void setHierarchy(LoggerRepository repository) {
	    this.repository = repository;
	  }
	  
	  public
	  final
	  String getName() {
	    return name;
	  }


	  final
	  public
	  Category getParent() {
	    return this.parent;
	  }


	  final
	  public
	  Level getLevel() {
	    return this.level;
	  }

	  final
	  public
	  Level getPriority() {
	    return this.level;
	  }
	  
	 */
	
	
	/*
	 * // FIXME Override package private?? Hope these methods are never called. 
	 * 
	 * 	
		@Override
		synchronized void closeNestedAppenders() {
			Ref.callMethod("closeNestedAppenders", logger, new Class<?>[] {}, new Object[] {});
		}
	*/	
	

}
