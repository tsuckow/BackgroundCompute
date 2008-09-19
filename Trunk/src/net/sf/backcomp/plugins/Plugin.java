/**
 * @(#)Plugin.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.plugins;

import javax.swing.*;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import java.nio.DoubleBuffer;
import java.util.*;

/**
 * This is the abstract class for any v1 Plugin.
 * <br>
 * This API is changing rapidly in pre-1.0 stages and may break Plugins. 
 *
 * @author Deathbob 
 * @version 0.3.0 2007/12/28
 */
public abstract class Plugin
{
	//private static final long serialVersionUID = 1L;
	//CONSTS
	
	/**
	 * The run state the plugin is in.
	 */
	public static enum PluginState
	{
		Initilizing, Running, Stopping, Stopped, Paused, Removing
	};
	
	/**
	 * Reason for not allowing the run of the plugin
	 */
	private static enum norunReason
	{
		Removal, Reload
	};
	
	
	//
	//PRIVATE
	//
	// These are part of the inner workings of the BackComp Plugin System.
	// These shouldn't be tampered with by any other class (Including Inherited Ones).
	//
	
	//Locks  //Create a REALLY SMALL but unique object.
	/**
	 * Locks whenever we want to change something relating to cores.
	 */
	private Object lock_core = new Object();
	/**
	 * Locks whenever we do something with whether the core manger is running.
	 */
	private Object lock_coreManager = new Object();
	
	private volatile PluginState currentState = PluginState.Stopped;//FIXME: Race condition Exists, Low Priority (Fixed?)
	
	//Variables
	
	private List<coreInstance> threadList = Collections.synchronizedList(new LinkedList<coreInstance>());
	
	private volatile long threadCount = 0;
	
	private static boolean stopAllPlugins = false;
	
	private volatile boolean paused = false;

	private volatile boolean coreManagerRunning = false;
	
	//Classes
	
	private static class coreInstance
	{
		private volatile Thread thread = null;
		private DoubleBuffer CPUbuffer = DoubleBuffer.allocate(20);
		
		public coreInstance(Thread t)
		{
			this.thread = t;
		}
		
		public Thread getThread()
		{
			return thread;
		}
		
		public volatile long measurementStartTime = 0;
		public volatile long measurementStartCPU = 0;
		
		public volatile long CPUThrottle = 100;
		public volatile boolean doCPUThrottle = false;
		
		public synchronized void CPUusagePut(double d)
		{
			if(CPUbuffer.remaining() == 0)
				CPUbuffer.rewind();
			CPUbuffer.put(d);
		}
		
		public synchronized double CPUusageGetAve()
		{
			final double[] usages = CPUbuffer.array();
			double Ave = 0;
			for( double usage : usages )
			{
				Ave += usage;
			}
			return Ave / usages.length;
		}
	}
	
	private final class coreManager extends Thread
	{
		@Override
		public void run()
		{
			ThreadMXBean TMB = ManagementFactory.getThreadMXBean();
			while(true)
			{
				synchronized(lock_core)
				{
					if(threadList.size() < threadCount)
					{
						currentState = PluginState.Initilizing;
						coreRunner cr = new coreRunner(threadList.size()==0);
						threadList.add(new coreInstance(cr));
						cr.setPriority(Thread.MIN_PRIORITY);
						cr.start();
					}
				
				
					Iterator<coreInstance> it = threadList.iterator();
					while(it.hasNext())
					{
						coreInstance ci = it.next();
						Thread th = ci.getThread();
						if(!th.isAlive())
						{
							it.remove();
						}
						else
						{
							//Do CPU Usage stuff
							if(new Date().getTime() * 1000000 - ci.measurementStartTime > 0.5 * 1000000000)
		    				{
								//Store Numbers
								if(new Date().getTime() * 1000000 - ci.measurementStartTime != 0)
									ci.CPUusagePut( ( TMB.getThreadCpuTime(th.getId()) - ci.measurementStartCPU) / (new Date().getTime() * 1000000.0 - ci.measurementStartTime) * 100.0 );
								
								//Reset
		    					ci.measurementStartTime = new Date().getTime() * 1000000;
		    					if( TMB.isThreadCpuTimeSupported() )
				    			{
		    						if(!TMB.isThreadCpuTimeEnabled())
				    				{
				    					TMB.setThreadCpuTimeEnabled(true);
				    				}
		    						
		    						ci.measurementStartCPU = TMB.getThreadCpuTime(th.getId());
				    			}
		    					
		    					final int cpuGoal = 60;
		    					
		    					//Adjust
		    					if(ci.CPUusageGetAve() > cpuGoal && ci.CPUusageGetAve() != 0)
		    					{
		    						ci.CPUThrottle += ci.CPUusageGetAve()-cpuGoal +1;
		    					}
		    					if(ci.CPUusageGetAve() < cpuGoal - 10 && ci.CPUusageGetAve() != 0)
		    					{
		    						ci.CPUThrottle -= (cpuGoal-10) - ci.CPUusageGetAve() + 1;
		    					}
		    					if(ci.CPUThrottle < 1) ci.CPUThrottle = 1;
		    					ci.doCPUThrottle = true;
		    					//BC.PError("CPU Test: " + ci.CPUusageGetAve());
		    					//Debug.message("CPU Test: " + ci.CPUusageGetAve(),DebugLevel.Debug);
		    				}
						}
					}
				}
				
				synchronized(lock_coreManager)
				{
					if(threadCount == 0)
					{
						if(threadList.size() == 0)
						{
							if(paused)
								currentState = PluginState.Paused;
							else
								currentState = PluginState.Stopped;
							coreManagerRunning = false;
							return;
						}
						currentState = PluginState.Stopping;
					}
				}
				
				try
		    	{
		    		Thread.sleep(500);
		    	}
		    	catch(InterruptedException e)
		    	{ 	}
			}
		}
	}
	
	private final class coreRunner extends Thread
	{
		private volatile boolean main = false;
		
		public coreRunner(boolean main)
		{
			this.main = main;
		}
		
		@Override
		public void run()
		{
			currentState = PluginState.Running;
			if(main)
			{
				
				main();
			}
			else
			{
				core();
			}
		}
	}
	
	private static final class noRun
	{
		private volatile int flag = 0;
		
		/**
		 * Sets a norun reason flag.
		 * 
		 * @param reason Flag to set
		 * @return True if flag was set; False if the flag was already set
		 */
		public final synchronized boolean setFlag(norunReason reason)
		{
			int MASK = (int) Math.pow(2, reason.ordinal() );
					
			if( (flag & MASK) == MASK )
			{
				return false;
			}
			else
			{
				flag |= MASK;
				return true;
			}		
		}
		
		public final synchronized boolean isSet(norunReason reason)
		{
			int MASK = (int) Math.pow(2, reason.ordinal() );
					
			return (flag & MASK) == MASK;	
		}
		
		/**
		 * Clears a norun reason flag.
		 * 
		 * @param reason Flag to clear
		 * @return True if flag was cleared; False if the flag was already cleared
		 */
		public final synchronized boolean clearFlag(norunReason reason)
		{
			int MASK = (int) Math.pow(2, reason.ordinal() );
			
			if( (flag & MASK) != MASK )
			{
				return false;
			}
			else
			{
				flag &= ~MASK;
				return true;
			}
		}
		
		public final boolean isOK()
		{
			return flag == 0;
		}
	}
    private final noRun norun = new noRun();
	
    //
	//PROTECTED
	//
	// These are part of the cross workings of the BackComp Plugin System.
	// These provide communication between BackComp and the Plugin.
    // Some also need to be overridden to actually make the Plugin, well, do something.
	//
    
    //Functions
    
    /**
     * Main Method of the Plugin. This is called when the Plugin is started.
     * Tip: If applicable you could override <code>core()</code> to point here.
     */
    abstract protected void main();
    
    /**
     * An additional CPU Core for processing
     * 
     * Remember to override needCore()
     * 
     */
    protected void core() {return;}
    
	/**
	 * Main Removal Method. This is called when the plugin should uninstall itself.
	 * Note: A java class can tamper with its own file while it is running.
	 */
	abstract protected void remove();
	
	/**
	 * Called by the plugin to force reload of Plugin.
	 * Sets a flag indicating to the PluginLoader it should
	 * remove it from cache and force Garbage Collection. 
	 */
	protected final void reload()
	{
		norun.setFlag(norunReason.Reload);
		Debug.message("Plugin Asked for Reload: " + getName(), DebugLevel.Information);
		stopAll(true,false);
	}
	
	/**
	 * Retrieves the average computed CPU usage for this plugin.
	 * 
	 * @return CPU usage
	 */
	protected final double getCpuUsage()
	{
		Thread ct = Thread.currentThread();
		synchronized(lock_core)
		{
			int index = -1;
			coreInstance ci = null;
			Iterator<coreInstance> it = threadList.iterator();
			while(it.hasNext())
			{
				ci = it.next();
				Thread th = ci.getThread();
				if( ct.equals(th) )
				{
					index = threadList.lastIndexOf(ci);
				}
			}
			
			if(index != -1)
			{
				return ci.CPUusageGetAve();
			}
			else
			{
				return -1;
			}
		}
	}
	
	final protected boolean currentCoreShouldExit()
	{
		Thread ct = Thread.currentThread();
		long sleeptime = 0;
		synchronized(lock_core)
		{
			int index = -1;
			coreInstance ci = null;
			Iterator<coreInstance> it = threadList.iterator();
			while(it.hasNext())
			{
				ci = it.next();
				Thread th = ci.getThread();
				if( ct.equals(th) )
				{
					index = threadList.lastIndexOf(ci);
				}
			}
			
			if(index != -1)
			{
				if(stopAllPlugins)
				{
					currentState = PluginState.Stopping;
	    			threadCount = 0;
					return true;
				}
				
				if(threadList.size() > threadCount && index == threadList.size()-1)
				{
					return true;
				}
			
				if(ci.doCPUThrottle)
				{
					ci.doCPUThrottle = false;
					sleeptime = ci.CPUThrottle;
					if(sleeptime < 1) sleeptime = 1;
				}
			}
		}
		
		try
    	{
			Thread.sleep(sleeptime);
    	}
    	catch(InterruptedException e)
    	{ 	}
		
		return false;
	}
	
	/**
     * Decides if another core is desired by the plugin.
     * 
     * Returns True - Another Processing Core Wanted ; False - Forfeit Core
     * 
     * @return Default: False - Never want a core.
     */
    protected boolean needCore(){return false;}
	
	//
	//PUBLIC
	//
	// These are part of the outer workings of the BackComp Plugin System.
	// These provide control of the Plugin by BackComp.
    // Some also need to be overridden to actually make the Plugin interact properly.
	//
	
	//Functions
    
    /**
	 * 
	 * Returns the name of the Plugin
	 * 
	 * @return Name of the Plugin
	 */
    abstract public String getName();
    
    /**
	 * 
	 * Retrieves the HTML to describe the Plugin.
	 * 
	 * @return HTML contained in a String Object.
	 */
    abstract public String getInfo();
    
    /**
     * JPanel to be placed in a dialog containing the Plugins Status.
     * 
     * @return JPanel of a Status Dialog.
     */
    public JPanel getStatus(){return null;}
    
    /**
     * JPanel to be placed in a dialog containing the Plugins Settings.
     * 
     * @return JPanel of a Settings Dialog.
     */
    public JPanel getSettings(){return null;}
    
    public final PluginState getState()
    {
    	return currentState;
    }
    
    /**
     * 
     * @return True if the plugin needs reload
     */
    public final boolean needReload()
    {
    	return norun.isSet(norunReason.Reload);
    }
    
    /**
     * Checks how many cores are currently active.
     * 
     * @return Number of running cores
     */
    public final long getRunningCores()
    {
    	return threadCount;
    }
    
    /**
     * Starts the main core or other cores if needed.
     * 
     * @return True if the core was started; False if core not wanted
     */
    public final boolean startCore()
    {
    	synchronized(lock_core)
		{
    		if(!norun.isOK() || !needCore()) return false;

    		paused = false;
    		threadCount += 1;
    		Debug.message("Starting core on Plugin: " + getName(), DebugLevel.Information);
    		
    		synchronized(lock_coreManager)
			{
    			if(coreManagerRunning == false)
    			{
    				coreManager cm = new coreManager();
    				cm.start();
    				coreManagerRunning = true;
    			}
			} 		
		}
    	return true;
    }
    
    /**
     * Stops 1 running core at the Plugin's convienience. If none are running it does nothing.
     */
    public final void stopCore(boolean includeMain)
    {
    	synchronized(lock_core)
		{
    		if(threadCount > ( includeMain ? 0 : 1 ) )
    		{
    			Debug.message("Stopping core on plugin: " + getName(), DebugLevel.Information);
    			threadCount -= 1;
    		}
		}
    }
    
    /**
     * Stops all running cores at the Plugin's convenience. If none are running it does nothing.
     * 
     * @param includeMain Quit main thread?
     * @param pause Pause after stop all cores? Only valid if includeMain == true.
     */
    public final void stopAll(boolean includeMain, boolean pause)
    {
    	synchronized(lock_core)
		{
    		if(includeMain && threadCount > 0)
    		{
    			currentState = PluginState.Stopping;
    			threadCount = 0;
    			if(pause)
    			{
    				paused = true;
    				Debug.message("Pausing Plugin: " + getName(), DebugLevel.Information);
    			}
    			else
    			{
    				Debug.message("Stopping Plugin: " + getName(), DebugLevel.Information);
    			}
    		}
    		else if(!includeMain && threadCount > 1)
    		{
    			threadCount = 1;
    			Debug.message("Stopping all subcores on Plugin: " + getName(), DebugLevel.Information);
    		}
		}
    }
    
    /**
     * Shuts down the plugin and starts the uninstall procedure.
     */
    public final void startRemove()
    {
    	synchronized(lock_core)
		{
    		norun.setFlag(norunReason.Removal); //Stop all new core operations.
		}
    	
    	currentState = PluginState.Removing;
    	Debug.message("Removing Plugin: " + getName(), DebugLevel.Information);
    	
    	stopAll(true,false); //Stop All Cores
    	
    	//FIXME: Check all cores are stopped. Size of threadList?
    	
    	currentState = PluginState.Removing;
    	
    	Debug.messageDlg("Tried to remove a plugin",DebugLevel.NotImplemented);
    	return;
    	
    	//remove(); //Call Plugins Removal Method
    	
    	//TODO: Remove from Plugin Cache and do garbage collection multiple times.
    }
    
    public final static void terminate()
    {
    	stopAllPlugins = true;
    }
}