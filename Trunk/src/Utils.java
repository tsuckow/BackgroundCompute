/**
 * @(#)Utils.java
 *
 *
 * @author 
 * @version 1.00 2007/2/11
 */
 
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Utils
{
	private Utils(){} //Since this only contains static members, prevent an instance of this class from being created.  
	
	//Locks
	final static Object lock_tray = new int[1];
	
	private static TrayIcon trayIcon = null;
	
	public static void iconCreate()
	{
		synchronized(lock_tray)
		{
			if ( SystemTray.isSupported() )
			{
				SystemTray tray = SystemTray.getSystemTray(); //Get the tray
				Image image = Toolkit.getDefaultToolkit().getImage("Tray.png"); //Get the tray image
				
				ActionListener listener = new iconListener();
				
				// create a popup menu
				PopupMenu popup = new PopupMenu();
				
				MenuItem Item = new MenuItem("Add Project");
	         	Item.addActionListener(listener);
         		Item.setActionCommand("Plugins"); 
         		
				popup.add(Item);
				
				Item = new MenuItem("Remove Project");
	         	Item.addActionListener(listener);
         		Item.setActionCommand("RemovePlugin"); 
         		
				popup.add(Item);
				
				Item = new MenuItem("Current Projects");
	         	Item.addActionListener(listener);
         		Item.setActionCommand("Plugins"); 
         		
				popup.add(Item);
				
				Item = new MenuItem("Settings");
	         	Item.addActionListener(listener);
         		Item.setActionCommand("Settings"); 
         		
				popup.add(Item);
         		
         		// create menu item for the default action
        		MenuItem defaultItem = new MenuItem("Quit");
         		defaultItem.addActionListener(listener);
         		defaultItem.setActionCommand("Quit"); 
         		
         		popup.add(defaultItem);
         		
         		// construct a TrayIcon
         		trayIcon = new TrayIcon(image, "Background Compute", popup);
         		trayIcon.setImageAutoSize(true);
         	
         		// set the TrayIcon properties
         		trayIcon.addActionListener(listener);
         		// ...
         	
         		// add the tray image
         		try
         		{
             		tray.add(trayIcon);
         		}
         		catch (AWTException e)
         		{
             		System.err.println(e);
         		}
        		// ...
         			
			}
			
			//Don't put this in the sync or it will deadlock ;)
			iconMessage("Tray Icon","This was created in utils",TrayIcon.MessageType.INFO);
		}
	}
	
	public static void iconMessage(String title, String msg, TrayIcon.MessageType type)
	{
		synchronized(lock_tray)
		{
			trayIcon.displayMessage(title,msg,type);
		}
	}
	
	private static class iconListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
        {
			if(e == null) return;
            if(e.getActionCommand() == null) return;
            if( e.getActionCommand().equals("Quit") )
            	System.exit(0);
            if( e.getActionCommand().equals("Plugins") )
            {
            	PluginManager.show();
            }
            
            if( e.getActionCommand().equals("RemovePlugin") )
            {
            	PluginRemover.show();
            }
            		
            if( e.getActionCommand().equals("Settings") )
            {
            	Settings.show();
			}
		}
	}
}