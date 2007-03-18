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
	private boolean running = false;
	
	private boolean stop = false;
	
    private synchronized void runner()
    {
    	running = true;
    	run();
    	running = false;
    	stop = false;
    }
    
    private synchronized void updater()
    {
    	running = true;
    	update();
    	running = false;
    	stop = false;
    }
    
    
    
    abstract protected void run(); //Should not be called by a plugin
    
	abstract protected void update(); //Should not be called by a plugin
    
    
    
    abstract public String[] getPluginInfo();
    
    abstract public int getState();
    
    abstract public JPanel getStatus();
    
    abstract public JPanel getSettings();
    
    abstract public void remove();
    
    public boolean isRunning()
    {
    	return running;
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
    
    public void start()
    {
    	runner();
    }    
}