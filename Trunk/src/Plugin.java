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
	protected static boolean running = false;
	
	protected static boolean stop = false;
	
	protected static boolean norun = false;
	
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
    	update();
    	running = false;
    	stop = false;
    }
    
    
    
    abstract protected void run(); //Should not be called by a plugin
    
	abstract protected void update(); //Should not be called by a plugin
    
    abstract protected void remove(); //Should not be called by a plugin
    
    
    
    abstract public String getName();
    
    abstract public String getInfo();
    
    abstract public int getState();
    
    abstract public JPanel getStatus();
    
    abstract public JPanel getSettings();
    
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
    	if(norun) return;
    	runner();
    }
    
    public void startUpdate()
    {
    	if(norun) return;
    	updater();
    }
    
    public void startRemove()
    {
    	norun = true;
    	while(running) stop();
    	remove();
    }  
}