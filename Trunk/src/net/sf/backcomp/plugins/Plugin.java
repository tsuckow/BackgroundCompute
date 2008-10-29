/**
 * @(#)Plugin.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.plugins;

import javax.swing.*;

/**
 * This is the interface for any v1 Plugin.
 * <br>
 * This API is changing rapidly in pre-1.0 stages and may break Plugins.
 * Like in October 2008
 * 
 * @author Deathbob 
 * @version 0.3.0 2007/12/28
 */
public interface Plugin
{
	public static final long serialVersionUID = 2L;
	
	//setPriority(Thread.MIN_PRIORITY);
	
	//Functions
	
	public void initialize(PluginInterconnect PI);
	
	/**
     * Shuts down the plugin and starts the uninstall procedure.
     */
    void uninstall();
    
    void start();
    
    void pause();
    
    void halt();
    
    /**
	 * 
	 * Returns the name of the Plugin
	 * 
	 * @return Name of the Plugin
	 */
    public String getName();
    
    /**
	 * 
	 * Retrieves the HTML to describe the Plugin.
	 * 
	 * @return HTML contained in a String Object.
	 */
    public String getInfo();
    
    /**
     * JPanel to be placed in a dialog containing the Plugins Settings.
     * 
     * @return JPanel of a Settings Dialog.
     */
    public JPanel getSettings();
    
    public JPanel getStatus();
}