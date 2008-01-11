/**
 * @(#)Plugin.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */

import javax.swing.*;

/**
 * This is the abstract class for any v1 Plugin.
 * 
 * This API is changing rapidly in pre-1.0 stages and may break Plugins. 
 *
 * @author Deathbob 
 * @version 0.2.5 2007/12/28
 */

abstract public class Plugin
{
	//CONSTS
	
	public static enum state {Initilizing, Running, Updating, Stopping, Stopped, Removing};
	
	private static enum norunReason {Removal, Update};
	
	//
	//PRIVATE
	//
	// These are part of the inner workings of the BackComp Plugin System.
	// These shouldn't be tampered with by any other class (Including Inherited Ones).
	//
	
	//Locks  //Create a REALLY SMALL but unique object.
	private final Object lock_core = new Object(); //Locks whenever we want to change something relating to cores.
	
	private volatile state currentState = state.Stopped;
	
	//Variables
	
	/**
	 * True: The Plugin is currently running in some shape or form.
	 * False: All operation has ceased.
	 */
	private boolean running = false;
	
	/**
	 * True: The Plugin is currently performing an update.
	 * False: The Plugin is not performing an update.
	 */
	private boolean updating = false;
	
	/**
	 * True: The Plugin is stopping whatever it is doing as soon as it safely can.
	 * False: The Plugin is running or stopped.
	 */
	private boolean stop = false;
	

	//Functions
	
	//Classes
	
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
     * Main Update Method of the Plugin.
     * Note: Calling this directly generally defeats the purpose of BackComp calling it.
     * 
     * @return Whether the Plugin needs to be reloaded because something changed.
     */
	protected boolean update(){return false;}
    
	/**
	 * Main Removal Method. This is called when the plugin should uninstall itself.
	 * Note: A java class can tamper with its own file while it is running.
	 */
	abstract protected void remove();
	
	/**
	 * Called by the plugin to start the update function after all cores have been terminated.
	 */
	protected final void startUpdate()
	{
		norun.setFlag(norunReason.Update);
		norun.clearFlag(norunReason.Update);
		//FIXME: Not thread safe.
		boolean result = update();
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
    	return 0;
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
    		//Create if need
    		
    		if(false /*need to start main?*/)
    		{
    			currentState = state.Initilizing;
    			
    		}
		}
    	return true;
    }
    
    /**
     * Stops 1 running core. If none are running it does nothing.
     */
    public final void stopCore(boolean includeMain)
    {
    	synchronized(lock_core)
		{
    		//TODO
		}
    }
    
    /**
     * Stops all running cores. If none are running it does nothing.
     */
    public final void stopAll(boolean includeMain)
    {
    	synchronized(lock_core)
		{
    		currentState = state.Stopping;
    		//TODO: Actually Stop
    		currentState = state.Stopped;
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
    	
    	stopAll(true); //Stop All Cores
    	
    	remove(); //Call Plugins Removal Method
    	
    	//TODO: Remove from Plugin Cache and do garbage collection multiple times.
    }
}