/**
 * @(#)Plugin.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */

import javax.swing.*;
import java.util.*;

/**
 * This is the abstract class for any v1 Plugin.
 * 
 * This API is changing rapidly in pre-1.0 stages and may break Plugins. 
 *
 * @author Deathbob 
 * @version 0.3.0 2007/12/28
 */

abstract public class Plugin
{
	//CONSTS
	
	/**
	 * The run state the plugin is in.
	 */
	public static enum state {Initilizing, Running, Stopping, Stopped, Removing};
	
	private static enum norunReason {Removal, Reload};
	
	//
	//PRIVATE
	//
	// These are part of the inner workings of the BackComp Plugin System.
	// These shouldn't be tampered with by any other class (Including Inherited Ones).
	//
	
	//Locks  //Create a REALLY SMALL but unique object.
	private final Object lock_core = new Object(); //Locks whenever we want to change something relating to cores.
	private final Object lock_coreManager = new Object(); //Locks whenever we do something with whether the core manger is running.
	
	private volatile state currentState = state.Stopped;
	
	//Variables
	
	private List<Thread> threadList = Collections.synchronizedList(new LinkedList<Thread>());
	
	private volatile long threadCount = 0;

	private volatile boolean coreManagerRunning = false;
	
	//Functions
	
	//Classes
	
	private final class coreManager extends Thread
	{
		@Override
		public void run()
		{
			while(true)
			{
				synchronized(lock_core)
				{
					if(threadList.size() < threadCount)
					{
						currentState = state.Initilizing;
						coreRunner cr = new coreRunner(threadList.size()==0);
						threadList.add(cr);
						cr.setPriority(Thread.MIN_PRIORITY);
						cr.start();
						currentState = state.Running;
					}
				
				
					Iterator<Thread> it = threadList.iterator();
					while(it.hasNext())
					{
						Thread th = it.next();
						if(!th.isAlive()) it.remove();
					}
				}
				
				synchronized(lock_coreManager)
				{
					if(threadCount == 0)
					{
						if(threadList.size() == 0)
						{
							currentState = state.Stopped;
							coreManagerRunning = false;
							return;
						}
						currentState = state.Stopping;
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
			if(main)
			{
				currentState = state.Running;
				main();
			}
			else
			{
				core();
			}
		}
	}
	
	private final class noRun
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
	 */
	protected final void reload()
	{
		norun.setFlag(norunReason.Reload);
		
		stopAll(true);
		
		BC.PError("Attempt to reload Plugin; Not Implemented"); return;
		//TODO: Reload Code
	}
	
	protected final boolean currentCoreShouldExit()
	{
		Thread ct = Thread.currentThread();
		synchronized(lock_core)
		{
			if(threadList.size() > threadCount && threadList.lastIndexOf(ct) == threadList.size()-1)
			{
				return true;
			}
		}
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
    
    public final state getState()
    {
    	return currentState;
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

    		threadCount += 1;
    		
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
    			threadCount -= 1;
		}
    }
    
    /**
     * Stops all running cores at the Plugin's convienience. If none are running it does nothing.
     */
    public final void stopAll(boolean includeMain)
    {
    	synchronized(lock_core)
		{
    		if(includeMain && threadCount > 0)
    		{
    			currentState = state.Stopping;
    			threadCount = 0;
    		}
    		else if(!includeMain && threadCount > 1)
    		{
    			threadCount = 1;
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
    	
    	currentState = state.Removing;
    	
    	stopAll(true); //Stop All Cores
    	
    	//FIXME: Check all cores are stopped. Size of threadList?
    	
    	currentState = state.Removing;
    	
    	BC.PError("Tried to remove a plugin; Not Implemented");
    	return;
    	
    	//remove(); //Call Plugins Removal Method
    	
    	//TODO: Remove from Plugin Cache and do garbage collection multiple times.
    }
}