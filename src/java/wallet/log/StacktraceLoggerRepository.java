package wallet.log;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;

public class StacktraceLoggerRepository implements LoggerRepository {
	
	protected final LoggerRepository repo;
	
	public StacktraceLoggerRepository(LoggerRepository repo) {
		this.repo = repo;		
	}

	public void addHierarchyEventListener(HierarchyEventListener listener) {
		repo.addHierarchyEventListener(listener);
	}
	
	public boolean isDisabled(int level) {
		return repo.isDisabled(level);
	}						
	
	public void setThreshold(Level level) {
		repo.setThreshold(level);
	}
	
	public void setThreshold(String val) {
		repo.setThreshold(val);
	}
	
	public void emitNoAppenderWarning(Category cat) {
		repo.emitNoAppenderWarning(cat);
	}					
	
	public Level getThreshold() {
		return repo.getThreshold();
	}						
	public Logger getLogger(String name) {
		Logger l = repo.getLogger(name);
		if (l == null || l instanceof StacktraceLogger) {
			return l;
		} else {
			return new StacktraceLogger(l);
		}
	}
	public Logger getLogger(String name, LoggerFactory factory) {
		Logger l = repo.getLogger(name, factory);
		if (l == null || l instanceof StacktraceLogger) {
			return l;
		} else {
			return new StacktraceLogger(l);
		}
	}
	public	Logger getRootLogger() {
		Logger l = repo.getRootLogger();
		if (l == null || l instanceof StacktraceLogger) {
			return l;
		} else {
			return new StacktraceLogger(l);
		}
	}
	public Logger exists(String name) {
		Logger l = repo.exists(name);
		if (l == null || l instanceof StacktraceLogger) {
			return l;
		} else {
			return new StacktraceLogger(l);
		}
	}
	public void shutdown() {
		repo.shutdown();
	}
	public Enumeration getCurrentLoggers() {
		return repo.getCurrentLoggers();
	}
	public Enumeration getCurrentCategories() {
		return repo.getCurrentCategories();
	}
	public void fireAddAppenderEvent(Category logger, Appender appender) {
		repo.fireAddAppenderEvent(logger, appender);
	}
	public void resetConfiguration() {
		repo.resetConfiguration();
	}
	

}
