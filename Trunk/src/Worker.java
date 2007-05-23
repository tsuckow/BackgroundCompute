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

public class Worker extends Thread
{	
    public Worker()
    {
    }
    
    public void run()
    {
    	
    	
    	//Hi
    	ClassLoader CL = BC.class.getClassLoader();
		
		URLClassLoader UCL = null;
		
		Plugin test = null;
		
		try
		{
			UCL = new URLClassLoader(new URL[]{new File("plugins/BP/").toURI().toURL()},CL);
		}
		catch(MalformedURLException ex)
		{
			BC.PError("Plugin Dir Path Malformed!");
		}
		try
		{
			
			test = (Plugin)UCL.loadClass("BP_Plugin").newInstance();
			
		}
		catch(ClassNotFoundException ex)
		{
			BC.PError("Class not found");
		}
		catch(InstantiationException ex)
		{
			BC.PError("Failed to load class");
		}
		catch(IllegalAccessException ex)
		{
			BC.PError("Illegal Access");
		}
		
		Utils.iconMessage("Loaded","Plugin has been loaded and will now be run...",TrayIcon.MessageType.INFO);
		
		test.start();
    }
}