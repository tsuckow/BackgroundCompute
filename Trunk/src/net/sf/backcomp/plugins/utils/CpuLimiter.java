/*
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */

package net.sf.backcomp.plugins.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;
import net.sf.backcomp.exceptions.ThreadCpuTimeNotSupportedException;

/**
 * Manages tracking CPU usage for a single thread.
 * <br>
 * This class is not thread safe and shouldn't need to be.
 * 
 * @author Deathbob
 *
 */
public class CpuLimiter
{
	private static final long NANOS_IN_MILLI = 100000;

	/**
	 * Bean for getting CPU usage information.
	 */
	private final ThreadMXBean mTMB = ManagementFactory.getThreadMXBean();
	
	/**
	 * Our CPU usage goal. (1-100)
	 */
	private int mCpuGoal = 30;
	
	/**
	 * One hundred percent.
	 */
	private static final int HUNDRED_PERCENT = 100;
	
	/**
	 * Real time of when <code>reset()</code> was called.
	 */
	private long mLastCheck;
	
	/**
	 * CPU usage time of current thread when <code>reset()</code> was called.
	 */
	private long mLastCpuTime;
	/**
	 * Sets up tracking of CPU usage for the calling thread.
	 * 
	 * @throws ThreadCpuTimeNotSupportedException When monitoring of CPU usage
	 * is not supported by the current JVM.
	 */
	public CpuLimiter() throws ThreadCpuTimeNotSupportedException
	{
		if ( !mTMB.isCurrentThreadCpuTimeSupported() )
		{
			Debug.message(
				"Thread CPU Time Not Supported",
				DebugLevel.NotImplemented );
			throw new ThreadCpuTimeNotSupportedException();
		}
		
		if ( !mTMB.isThreadCpuTimeEnabled() )
		{
			mTMB.setThreadCpuTimeEnabled( true );
		}
		
		reset();
	}
	
	/**
	 * Returns total amount of time the current thread has been executing on a
	 * CPU, in nanoseconds.
	 * @return Time on a CPU in nanoseconds.
	 */
	public long getThreadCpuTime()
	{
		return mTMB.getThreadCpuTime( Thread.currentThread().getId() );
	}
	
	/**
	 * Resets the usage start times. Further computations will be based on this
	 * moment in time.
	 */
	public void reset()
	{
		//Prepare for next round.
		mLastCheck = System.nanoTime();
		mLastCpuTime = getThreadCpuTime();
	}
	
	/**
	 * Returns the average CPU usage of the period from the last reset and now.
	 * @return CPU Usage
	 */
	public int getAvgCpuUsage()
	{
		final long currentCpu = getThreadCpuTime();
		final long currentTime = System.nanoTime();
		final long cpuInterval = currentCpu - mLastCpuTime;
		final long timeInterval = currentTime - mLastCheck;
		
		//If no time passed, forgetaboutit
		if ( cpuInterval == 0 || timeInterval == 0 )
		{
			return 0;
		}
		
		if ( cpuInterval > timeInterval )
		{
			Debug.message( "Paradox: Cpu time greater than time elapsed.\n"
				+ cpuInterval + "\n" + timeInterval, DebugLevel.Warning );
			return HUNDRED_PERCENT;
		}
		
		final long t = cpuInterval * HUNDRED_PERCENT;
		final long b = timeInterval;
		
		//Does t/b in integer division rounding up.
		return (int) ( ( t / b ) + ( ( t % b == 0 ) ? 0 : 1 ) );
	}
	
	/**
	 * Suspends the current thread for as long as needed to bring the average
	 * CPU usage back to the Goal. It is recommended that <code>reset()</code>
	 * be called every few seconds to keep the average accurate as periods of in
	 * activity may skew the average.
	 */
	public void doSleep()
	{
		long totalsleep = 0;
		while ( getAvgCpuUsage() > mCpuGoal )
		{
			final long currentCpu = getThreadCpuTime();
			final long currentTime = System.nanoTime();
			final long cpuInterval = currentCpu - mLastCpuTime;
			final long timeInterval = currentTime - mLastCheck;
			
			final long targetTimeInterval =
				( cpuInterval * (long) HUNDRED_PERCENT ) / (long) mCpuGoal;
			
			final long targetEndTime = targetTimeInterval + mLastCheck;
			
			final long sleeptime =
				( targetTimeInterval - timeInterval ) / NANOS_IN_MILLI /10;
			
			//if ( sleeptime > 0 )
			//{
				/*Debug.message(
					"Sleep time: " + Long.toString( currentCpu ) + " " + Long.toString( mLastCpuTime ) + " " + Long.toString( currentTime ) + " " + Long.toString( mLastCheck ),
					DebugLevel.Debug );*/
				Debug.message(
					"Sleep time: " + Long.toString( sleeptime ),
					DebugLevel.Debug );
			//}
			
			final long realsleep = ( sleeptime > 0 ) ? sleeptime : 1;
			
			totalsleep += realsleep;
			
			sleep( realsleep );
		}
			Debug.message(
				"Total Sleep:" + Long.toString( totalsleep ),
				DebugLevel.Debug );
	}
	
	/**
	 * Sleeps for <code>ms</code> milliseconds.
	 * @param ms Time to sleep in milliseconds.
	 */
	public static void sleep( final long ms )
	{
		try
		{
			Thread.sleep( ms );
		}
		catch ( final InterruptedException e )
		{
			//Since we can't deal with it, pass it on. 
			Thread.currentThread().interrupt();
		}
	}
}
