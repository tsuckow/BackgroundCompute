/*
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */

package net.sf.backcomp.plugins.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

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
	private final int BUFFER_SIZE = 20;
	private final ThreadMXBean TMB = ManagementFactory.getThreadMXBean();
	private Thread thisThread = Thread.currentThread();
	
	private long CpuGoal = 6000;
	private long lastCheck = 0;
	private long lastCpuTime = 0;
	private int CpuSamples[] = new int[BUFFER_SIZE];
	private int samplesIndex = 0;
	private int throttleTime = 0;
	
	
	/**
	 * Sets up tracking of CPU usage for the calling thread.
	 * 
	 * @throws ThreadCpuTimeNotSupportedException
	 */
	public CpuLimiter() throws ThreadCpuTimeNotSupportedException
	{
		if( !TMB.isCurrentThreadCpuTimeSupported() )
		{
			throw new ThreadCpuTimeNotSupportedException();
		}
		
		if(!TMB.isThreadCpuTimeEnabled())
		{
			TMB.setThreadCpuTimeEnabled(true);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public long getThreadCpuTime()
	{
		return TMB.getThreadCpuTime(thisThread.getId());
	}
	
	public void recordCpuUsage()
	{
		long currentCpu = getThreadCpuTime();
		long currentTime = System.nanoTime();
		long cpuInterval = currentCpu - lastCpuTime;
		long timeInterval = currentTime - lastCheck;
		
		//If no time passed, forgetaboutit
		if( cpuInterval == 0 || timeInterval == 0 ) return;
		
		//Usage % where 100% = 10,000 (100*100)
		int usageInterval = (int)( cpuInterval * 100 * 100 / timeInterval );
		
		//Prepare for next round,
		lastCpuTime = currentCpu;
		lastCheck = currentTime;
		
		//Commit the usage number
		CpuSamples[samplesIndex++] = usageInterval;
		if(samplesIndex > BUFFER_SIZE) samplesIndex = 0;
		
		//Recalculate sleep time
		throttleTime += (getAvgCpuUsage() - CpuGoal)/100;
		if(throttleTime < 0) throttleTime = 0;
	}
	
	public int getAvgCpuUsage()
	{
		int Ave = 0;
		for( int usage : CpuSamples )
		{
			Ave += usage;
		}
		return Ave / BUFFER_SIZE;
	}
	
	public int getSleepTime()
	{
		return throttleTime;
	}
	
	public void doSleep()
	{
		try
    	{
			Thread.sleep( getSleepTime() );
    	}
    	catch(InterruptedException e)
    	{
    		
    	}
	}
	
}
