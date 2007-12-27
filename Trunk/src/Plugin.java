/**
 * @(#)Plugin.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */

import javax.swing.*;

/**
 * This is a test Description
 *
 * @author Deathbob 
 * @version 0.2 2007/2/11
 */

abstract public class Plugin
{
	protected boolean running = false;
	
	protected boolean updating = false;
	
	protected boolean stop = false;
	
	protected boolean norun = false;
	
    protected synchronized void runner()
    {
    	running = true;
    	run();
    	running = false;
    	stop = false;
    }
    
    protected synchronized void updater()
    {
    	running = true;
    	updating = true;
    	update();
    	updating = false;
    	running = false;
    	stop = false;
    }
    
    //FIXME: Protected
    /**
     * Main function of Plugin
     * DO NOT CALL DIRECTLY - CALL start()
     * 
     */
    public void run()
    {
    	return;
    }
    
    //FIXME: Protected
    /**
     * Main update function of Plugin.
     * If returning TRUE, Plugin SHOULD set norun to TRUE to prevent problems.
     * DO NOT CALL DIRECTLY - CALL startUpdate()
     * 
     * @return TRUE - If restart of app is needed; False - No restart needed
     */
	public boolean update()
	{
		return false;
	}
    
    abstract public void remove(); //Should not be called by a plugin  
    
    /**
	 * 
	 * Retrieves the name of the Plugin
	 * 
	 * @return name of Plugin
	 */
    abstract public String getName();
    
    /**
	 * 
	 * Retrieves the HTML to describe the Plugin
	 * 
	 * @return HTML 
	 */
    abstract public String getInfo();
    
    abstract public int getState();
    
    abstract public JPanel getStatus();
    
    abstract public JPanel getSettings();
    
    public boolean isRunning()
    {
    	return running;
    }
    
    public boolean isUpdating()
    {
    	return running?updating:false;
    }
    
    public boolean isStopping()
    {
    	return stop;
    }
    
    public void stop()
    {
    	if(running)
    		stop = true;
    }
    
    /**
     * Decides if another core is desired by the plugin.
     * 
     * Returns True - Another Processing Core Wanted ; False - Forfeit Core
     * 
     * Must be overwritten to be used.
     * 
     * 
     *  
     * @return False - Never want a core
     */
    public boolean needCore()
    {
    	return false;
    }
    
    /**
     * An additional CPU Core for processing
     * 
     * Must be overwritten to be used.
     * 
     */
    protected void Core() {return;}
    
    /**
     * Starts the plugin or if it is already running spawns a new core (If Desired)
     * 
     * @see #needCore()
     */
    public void start()
    {
    	if(norun) return;
    	if(running)
    	{
    		if( needCore() )
    		{
    			Core();
    		}
    		else
    		{
    			return;
    		}
    	}
    	runner();
    }
    
    public void startUpdate()
    {
    	if(norun) return;
    	while(running) stop();
    	updater();
    }
    
    public void startRemove()
    {
    	norun = true;
    	while(running) stop();
    	remove();
    }  
}