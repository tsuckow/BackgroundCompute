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
	//
	//PRIVATE
	//
	// These are part of the inner workings of the BackComp Plugin System.
	// These shouldn't be tampered with by any other class (Including Inherited Ones).
	//
	
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
	
	/**
	 * True: The plugin has been locked from running for some reason. (Cannot be undone)
	 *       Note: If the plugin is already running, this will not stop it.
	 * False: It is fine for the plugin to run.
	 */
	private boolean norun = false;
	
	//Functions
	
	/**
	 * Handles starting the plugin's main method.
	 */
    private final synchronized void runner()
    {
    	//TODO: ADD Additional Sanity Checks Here
    	running = true;
    	run();
    	running = false;
    	stop = false;
    }
    
    /**
	 * Handles starting the plugin's update method.
	 */
    private final synchronized void updater()
    {
    	//TODO: ADD Additional Sanity Checks Here
    	running = true;
    	updating = true;
    	update();
    	updating = false;
    	running = false;
    	stop = false;
    }
    
	/**
	 * Handles starting the plugin's remove method.
	 */
    private final synchronized void remover()
    {
    	//TODO: ADD Additional Sanity Checks Here
    	norun = true;
    	running = true;
    	remove();
    	running = false;
    	stop = false;
    }
    
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
    abstract protected void run();
    
    /**
     * An additional CPU Core for processing
     * 
     * Must be overwritten to be used.
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
    
    /**
     * Returns whether the Plugin is currently doing something.
     * 
     * @return Whether the Plugin is running.
     */
    public final boolean isRunning()
    {
    	return running;
    }
    
    /**
     * Returns whether the Plugin is currently updating.
     * 
     * @return Whether the Plugin is updating.
     */
    publicf final boolean isUpdating()
    {
    	return running?updating:false;
    }
    
    /**
     * Returns whether the Plugin is currently stopping.
     * 
     * @return Whether the Plugin is stopping.
     */
    public final boolean isStopping()
    {
    	return stop;
    }
    
    /**
     * Stops the Plugin cleanly from whatever it is doing.
     */
    public final void stop()
    {
    	if(running)
    		stop = true;
    }
    
    /**
     * Decides if another core is desired by the plugin.
     * 
     * Returns True - Another Processing Core Wanted ; False - Forfeit Core
     * 
     * @return False - Never want a core.
     */
    public boolean needCore(){return false;}
    

    
    /**
     * Starts the plugin or if it is already running spawns a new core (If Desired)
     * 
     * @see #needCore()
     */
    public final void start()
    {
    	if(norun) return;
    	if(running)
    	{
    		if( needCore() )
    		{
    			core();
    		}
    		else
    		{
    			return;
    		}
    	}
    	runner();
    }
    
    /**
     * Forces an update. Stops any previous operations.
     */
    public final void startUpdate()
    {
    	if(norun || updating) return;
    	while(running) stop();
    	updater();
    }
    
    /**
     * Forces a removal (uninstall) of the Plugin. Stops any previous operations.
     */
    public final void startRemove()
    {
    	norun = true;
    	while(running) stop();
    	remover();
    }  
}