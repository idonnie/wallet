package wallet.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * Blocks current task execution if there is not enough resources for it.
 * Maximum task count usage controlled by maxTaskCount property.
 */
public class BlockingThreadPoolExecutor extends ThreadPoolExecutor {
	
	private static Logger logger = Logger.getLogger(BlockingThreadPoolExecutor.class);

    private final ReentrantLock taskLock = new ReentrantLock();
    private final Condition unpaused = taskLock.newCondition();
    private final int maxTaskCount;

    private volatile int currentTaskCount;

    public BlockingThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
    		long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, int maxTaskCount, final String name) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(name), new ThreadPoolExecutor.AbortPolicy());
        this.maxTaskCount = maxTaskCount;
    }

    /**
     * Executes task if there is enough system resources for it. Otherwise
     * waits.
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        taskLock.lock();
        try {
            // Spin while we will not have enough capacity for this job
            while (maxTaskCount < currentTaskCount) {
                try {
                	logger.debug("Number of active tasks exceeded: " + currentTaskCount + " > " + maxTaskCount);
                    unpaused.await();
                } catch (InterruptedException e) {
                    t.interrupt();
                }
            }
            currentTaskCount++;
        } finally {
            taskLock.unlock();
        }
    }

    /**
     * Signalling that one more task is welcome
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        taskLock.lock();
        try {
            currentTaskCount--;
            unpaused.signalAll();
        } finally {
            taskLock.unlock();
        }
    }
}
