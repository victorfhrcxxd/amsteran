package phantom.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class ThreadPool
{
	protected static final Logger _log = Logger.getLogger(ThreadPool.class.getName());
	
	private static final long MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2;
	
	private static int _threadPoolRandomizer;
	
	protected static ScheduledThreadPoolExecutor[] _scheduledPools;
	protected static ThreadPoolExecutor[] _instantPools;
	
	public static void init()
	{
		// Feed scheduled pool.
		int poolCount = -1;
		if (poolCount == -1)
			poolCount = Runtime.getRuntime().availableProcessors();
		
		_scheduledPools = new ScheduledThreadPoolExecutor[poolCount];
		for (int i = 0; i < poolCount; i++)
			_scheduledPools[i] = new ScheduledThreadPoolExecutor(1500);
		
		// Feed instant pool.
		poolCount = -1;
		if (poolCount == -1)
			poolCount = Runtime.getRuntime().availableProcessors();
		
		_instantPools = new ThreadPoolExecutor[poolCount];
		for (int i = 0; i < poolCount; i++)
			_instantPools[i] = new ThreadPoolExecutor(16, 16, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100000));
		
		// Prestart core threads.
		for (ScheduledThreadPoolExecutor threadPool : _scheduledPools)
			threadPool.prestartAllCoreThreads();
		
		for (ThreadPoolExecutor threadPool : _instantPools)
			threadPool.prestartAllCoreThreads();
		
		// Launch purge task.
		scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				for (ScheduledThreadPoolExecutor threadPool : _scheduledPools)
					threadPool.purge();
				
				for (ThreadPoolExecutor threadPool : _instantPools)
					threadPool.purge();
			}
		}, 600000, 600000);
	}
	
	/**
	 * Schedules a one-shot action that becomes enabled after a delay. The pool is chosen based on pools activity.
	 * @param r : the task to execute.
	 * @param delay : the time from now to delay execution.
	 * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion.
	 */
	public static ScheduledFuture<?> schedule(Runnable r, long delay)
	{
		try
		{
			return getPool(_scheduledPools).schedule(new TaskWrapper(r), validate(delay), TimeUnit.MILLISECONDS);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Schedules a periodic action that becomes enabled after a delay. The pool is chosen based on pools activity.
	 * @param r : the task to execute.
	 * @param delay : the time from now to delay execution.
	 * @param period : the period between successive executions.
	 * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation.
	 */
	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay, long period)
	{
		try
		{
			return getPool(_scheduledPools).scheduleAtFixedRate(new TaskWrapper(r), validate(delay), validate(period), TimeUnit.MILLISECONDS);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Executes the given task sometime in the future.
	 * @param r : the task to execute.
	 */
	public static void execute(Runnable r)
	{
		try
		{
			getPool(_instantPools).execute(new TaskWrapper(r));
		}
		catch (Exception e)
		{
		}
	}
	
	/**
	 * Retrieve stats of current running thread pools.
	 */
	public static void getStats()
	{
		for (int i = 0; i < _scheduledPools.length; i++)
		{
			final ScheduledThreadPoolExecutor threadPool = _scheduledPools[i];
			
			_log.info("=================================================");
			_log.info("Scheduled pool #" + i + ":");
			_log.info("\tgetActiveCount: ...... " + threadPool.getActiveCount());
			_log.info("\tgetCorePoolSize: ..... " + threadPool.getCorePoolSize());
			_log.info("\tgetPoolSize: ......... " + threadPool.getPoolSize());
			_log.info("\tgetLargestPoolSize: .. " + threadPool.getLargestPoolSize());
			_log.info("\tgetMaximumPoolSize: .. " + threadPool.getMaximumPoolSize());
			_log.info("\tgetCompletedTaskCount: " + threadPool.getCompletedTaskCount());
			_log.info("\tgetQueuedTaskCount: .. " + threadPool.getQueue().size());
			_log.info("\tgetTaskCount: ........ " + threadPool.getTaskCount());
		}
		
		for (int i = 0; i < _instantPools.length; i++)
		{
			final ThreadPoolExecutor threadPool = _instantPools[i];
			
			_log.info("=================================================");
			_log.info("Instant pool #" + i + ":");
			_log.info("\tgetActiveCount: ...... " + threadPool.getActiveCount());
			_log.info("\tgetCorePoolSize: ..... " + threadPool.getCorePoolSize());
			_log.info("\tgetPoolSize: ......... " + threadPool.getPoolSize());
			_log.info("\tgetLargestPoolSize: .. " + threadPool.getLargestPoolSize());
			_log.info("\tgetMaximumPoolSize: .. " + threadPool.getMaximumPoolSize());
			_log.info("\tgetCompletedTaskCount: " + threadPool.getCompletedTaskCount());
			_log.info("\tgetQueuedTaskCount: .. " + threadPool.getQueue().size());
			_log.info("\tgetTaskCount: ........ " + threadPool.getTaskCount());
		}
	}
	
	/**
	 * Shutdown thread pooling system correctly. Send different informations.
	 */
	public static void shutdown()
	{
		try
		{
			System.out.println("ThreadPool: Shutting down.");
			
			for (ScheduledThreadPoolExecutor threadPool : _scheduledPools)
				threadPool.shutdownNow();
			
			for (ThreadPoolExecutor threadPool : _instantPools)
				threadPool.shutdownNow();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	/**
	 * @param <T> : The pool type.
	 * @param threadPools : The pool array to check.
	 * @return the less fed pool.
	 */
	private static <T> T getPool(T[] threadPools)
	{
		return threadPools[_threadPoolRandomizer++ % threadPools.length];
	}
	
	/**
	 * @param delay : The delay to validate.
	 * @return a secured value, from 0 to MAX_DELAY.
	 */
	private static long validate(long delay)
	{
		return Math.max(0, Math.min(MAX_DELAY, delay));
	}
	
	public static final class TaskWrapper implements Runnable
	{
		private final Runnable _runnable;
		
		public TaskWrapper(Runnable runnable)
		{
			_runnable = runnable;
		}
		
		@Override
		public void run()
		{
			try
			{
				_runnable.run();
			}
			catch (RuntimeException e)
			{
				_log.warning("Exception in a ThreadPool task execution.");
				e.printStackTrace();
			}
		}
	}
}