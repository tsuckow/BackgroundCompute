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
 * <br>This class is not thread safe and shouldn't need to be.
 * 
 * @author Deathbob
 *
 */
public class CpuLimiter
{
	private final static int BUFFER_SIZE = 20;
	private final ThreadMXBean TMB = ManagementFactory.getThreadMXBean();
	
	private final long CpuGoal = 50 * 100;
	private long lastCheck = 0;
	private long lastCpuTime = 0;
	private final int CpuSamples[] = new int[BUFFER_SIZE];
	private int samplesIndex = 0;
	private int throttleTime = 0;
	
	/**
	 * Sets up tracking of CPU usage for the calling thread.
	 * 
	 * @throws ThreadCpuTimeNotSupportedException
	 */
	public CpuLimiter() throws ThreadCpuTimeNotSupportedException
	{
		if ( !TMB.isCurrentThreadCpuTimeSupported() )
		{
			Debug.message(
				"Thread CPU Time Not Supported",
				DebugLevel.NotImplemented );
			throw new ThreadCpuTimeNotSupportedException();
		}
		
		if ( !TMB.isThreadCpuTimeEnabled() )
		{
			TMB.setThreadCpuTimeEnabled( true );
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public long getThreadCpuTime()
	{
		return TMB.getThreadCpuTime( Thread.currentThread().getId() );
	}
	
	public void recordCpuUsage()
	{
		final long currentCpu = getThreadCpuTime();
		final long currentTime = System.nanoTime();
		final long cpuInterval = currentCpu - lastCpuTime;
		final long timeInterval = currentTime - lastCheck;
		
		//If no time passed, forgetaboutit
		if ( cpuInterval == 0 || timeInterval == 0 )
		{
			return;
		}
		if ( cpuInterval > timeInterval )
		{
			Debug.message( "Paradox: Cpu time greater than time elapsed.\n"
				+ cpuInterval + "\n" + timeInterval, DebugLevel.Warning );
			return;
		}
		
		//Usage % where 100% = 10,000 (100*100)
		final int usageInterval =
			( int ) ( cpuInterval * 100 * 100 / timeInterval );
		
		//Prepare for next round.
		lastCheck = System.nanoTime();
		lastCpuTime = getThreadCpuTime();
		
		//Commit the usage number
		CpuSamples[samplesIndex++] = usageInterval;
		if ( samplesIndex >= BUFFER_SIZE )
		{
			samplesIndex = 0;
		}
		
		//Recalculate sleep time
		throttleTime +=
			( getAvgCpuUsage() - CpuGoal ) / ( 100 )
				+ ( int ) Math.signum( getAvgCpuUsage() - CpuGoal );
		if ( throttleTime < 0 )
		{
			throttleTime = 0;
		}
	}
	
	public int getAvgCpuUsage()
	{
		int Ave = 0;
		for ( final int usage : CpuSamples )
		{
			Ave += usage;
		}
		return Ave / BUFFER_SIZE;
	}
	
	public int getSleepTime()
	{
		return throttleTime / 10;
	}
	
	public void doSleep()
	{
		sleep( getSleepTime() );
	}
	
	static public void sleep( final long ms )
	{
		try
		{
			Thread.sleep( ms );
		}
		catch ( final InterruptedException e )
		{
			
		}
	}
}
