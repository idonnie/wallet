package wallet.log;

import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;

public class StacktraceRepositorySelector implements RepositorySelector {
	
	protected final LoggerRepository repo = LogManager.getLoggerRepository();
	
	public LoggerRepository getLoggerRepository() {		
		return new StacktraceLoggerRepository(repo);
	}

}
