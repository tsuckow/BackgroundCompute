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
		Plugin test = Utils.loadPlugin("BP");
			
		Utils.iconMessage("Loaded","Plugin has been loaded and will now be run...",TrayIcon.MessageType.INFO);
		
		try
		{
			Thread.sleep(2000);
		}
		catch(InterruptedException e)
		{
			
		}
		
		test.start();
    }
}