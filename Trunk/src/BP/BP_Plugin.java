/**
 * @(#)BC_Plugin.java
 *
 *
 * @author 
 * @version 1.00 2007/2/11
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class BP_Plugin extends Plugin {

    public BP_Plugin() {
    }
    
    int i = 0;
    
    protected void run()
    {
    	while(true)
    	{
    		Utils.iconMessage("Thread Test in plugin!","Message: " + i,TrayIcon.MessageType.INFO);
    		try
    		{
    			Thread.sleep(10000);
    		}
    		catch(InterruptedException e)
    		{
    		}
    		i++;
    		try
    		{
    			Thread.sleep(1);
    		}
    		catch(InterruptedException e)
    		{
    		}
    		
    		if(isStopping()) return;
    	}
    }
    
    public String getName()
    {
    	return "Background Pi";
    }
    
    public String getInfo()
    {
    	return "<html>Hi<img src='http://defcon1.hopto.org/Title2.gif'></html>";
    }
    
    public JPanel getSettings()
    {
    	return null;
    }
    
    public JPanel getStatus()
    {
    	return null;
    }
    
    public void remove()
    {
    	BC.PError("Removing...");
    }
    
    public int getState()
    {
    	return 0;
    }
    
    protected void update()
    {
    	
    }
}