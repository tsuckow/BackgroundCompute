/**
 * @(#)Plugin.java
 *
 *
 * @author 
 * @version 1.00 2007/2/11
 */

import javax.swing.*;

abstract public class Plugin
{
	protected boolean running = false;
	
	protected boolean updating = false;
	
	protected boolean stop = false;
	
	protected boolean norun = false;
	
	protected int maxCores = 1;
	
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
    
    
    /**
     * Main function of Plugin
     * DO NOT CALL DIRECTLY - CALL start()
     * 
     * @author Deathbob
     */
    public void run()
    {
    	return;
    }
    
    /**
     * Main update function of Plugin
     * If returning TRUE, Plugin SHOULD set norun to TRUE to prevent problems.
     * DO NOT CALL DIRECTLY - CALL startUpdate()
     * 
     * @author Deathbob
     * @return TRUE - If restart of app is needed; False - No restart needed
     */
	public boolean update()
	{
		return false;
	}
    
    abstract public void remove(); //Should not be called by a plugin
    
    /**
	 * 
	 * Retrieves the number of preferred cores by the Plugin. The Plugin will be givin this many cores provided it is not greater than availCores.
	 * 
	 * @author Deathbob 
	 * @param availCores Number of cores the plugin has to work with.
	 * @return number of preferred cores or 0 to indicate desire to skip.
	 */
    public int preferredCores(int availCores)
    {
    	if(availCores > 0 && !norun && !running)
    		return 1;
    	else
    		return 0;
    }
    
    
    /**
	 * 
	 * Retrieves the name of the Plugin
	 * 
	 * @author Deathbob 
	 * @return name of Plugin
	 */
    abstract public String getName();
    
    /**
	 * 
	 * Retrieves the HTML to describe the Plugin
	 * 
	 * @author Deathbob 
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
    
    public void start(int cores)
    {
    	if(norun || running || cores <= 0) return;
    	maxCores = cores;
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