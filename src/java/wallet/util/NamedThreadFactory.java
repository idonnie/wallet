package wallet.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

public class NamedThreadFactory implements ThreadFactory {
	
	private final String threadName;
	private final AtomicLong threadNo = new AtomicLong(-1L);

    public NamedThreadFactory(final String threadName) {
        this.threadName = threadName;	
    }
    
	public Thread newThread(final Runnable r) {
		SecurityManager s = System.getSecurityManager();
		ThreadGroup g = (s != null
		    ? s.getThreadGroup()
			: Thread.currentThread().getThreadGroup());
		Thread t = new Thread(g, new RunnableWithLogging(r), threadName + "-" + threadNo.incrementAndGet(), 0);
		if (t.isDaemon())
		    t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
		    t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
	
	public static final class RunnableWithLogging implements Runnable {
		
		private final static Logger logger = Logger.getLogger(RunnableWithLogging.class);
		
		private final Runnable actualRunnable;
		
		public RunnableWithLogging(final Runnable r) {
			Fix.require(r != null, "r");
			actualRunnable = r instanceof RunnableWithLogging ? ((RunnableWithLogging) r).actualRunnable : r;
		}

		@Override
		public void run() {		
			try {
				actualRunnable.run();
			} catch (RuntimeException e) {
				logger.error("", e);
				throw e;
			} catch (Exception e) { 
				logger.error("", e);
				throw new RuntimeException(e);
			} catch (Error e) {
				logger.error("", e);
				throw e;
			} catch (Throwable e) {
				logger.error("", e);
				throw new RuntimeException(e);
			}
		}		
		
		@Override
		public boolean equals(Object o) {
			return o != null && (
				(this == o) 
				|| (o instanceof RunnableWithLogging && actualRunnable.equals(((RunnableWithLogging)o).actualRunnable))
			); 
		}
		
		@Override
		public int hashCode() {
			return actualRunnable.hashCode();
		}
		
	}
	
	public static final class CallableWithLogging<T> implements Callable<T> {
		
		private final static Logger logger = Logger.getLogger(CallableWithLogging.class);
		
		private final Callable<T> actualCallable;
		
		public CallableWithLogging(final Callable<T> c) {
			Fix.require(c != null, "c");
			actualCallable = c instanceof CallableWithLogging ? ((CallableWithLogging<T>) c).actualCallable : c;
		}

		@Override
		public T call() throws Exception {
			try {
				T result = actualCallable.call();
				return result;				
			} catch (Exception e) {
				logger.error("", e);
				throw e;
			} catch (Error e) {				
				logger.error("", e);
				throw e;
			} catch (Throwable e) {				
				logger.error("", e);
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public boolean equals(Object o) {
			return o != null && (
				(this == o) 
				|| (o instanceof CallableWithLogging && actualCallable.equals(((CallableWithLogging<T>)o).actualCallable))
			); 
		}
		
		@Override
		public int hashCode() {
			return actualCallable.hashCode();
		}
		
	}	
	
}