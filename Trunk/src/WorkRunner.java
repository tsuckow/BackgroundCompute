/**
 * @(#)Worker.java
 *
 *
 * @author 
 * @version 1.00 2007/2/10
 */
 
import java.io.*;//File

import java.net.*; //URL

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class WorkRunner extends Thread
{	
	public void end()
	{
		
	}
	

    public void run()
    {
    	while(true)
    	{
    		String[] Plugins = Utils.getLocalPlugins();
    		for(int i = 0; i < Plugins.length; ++i)
    		{
    			Plugin plug = Utils.loadPlugin(Plugins[i]);
    			if(!plug.isRunning())
    			{
    				plug.update();
    				plug.start();
    			}
    			Plugins = Utils.getLocalPlugins();
    		}
    	}
    	/*
    	//Hi
		Plugin test = Utils.loadPlugin("BP");
			
		Utils.iconMessage("Loaded","Plugin has been loaded and will now be run...",TrayIcon.MessageType.INFO);
		
		try
		{
			Thread.sleep(2000);
		}
		catch(InterruptedException e)
		{
			
		}
		
		test.start();*/
    }
}