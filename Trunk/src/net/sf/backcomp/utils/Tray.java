/**
 * @(#)Tray.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.utils;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;
import net.sf.backcomp.dialogs.*;
import net.sf.backcomp.plugins.Plugin;
import net.sf.backcomp.plugins.PluginLoader;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Deathbob
 *
 */
public final class Tray
{
	private Tray(){}//This is a static class
	
	//Locks
	private final static Object lock_tray = new Object();//Create a REALLY SMALL but unique object.
	
	private static TrayIcon trayIcon = null;
	
	/**
	 * 
	 * Creates a Tray Icon if one does not already exist.
	 * 
	 * 
	 */
	
	public static void iconCreate()
	{
		//TODO: Make sure only one instance
		synchronized(lock_tray)
		{
			if ( SystemTray.isSupported() )
			{
				SystemTray tray = SystemTray.getSystemTray(); //Get the tray
				Image image = Toolkit.getDefaultToolkit().getImage("Tray.png"); //Get the tray image
				
				ActionListener listener = new iconListener();
				
				// create a popup menu
				PopupMenu popup = new PopupMenu();
				
				MenuItem Item = new MenuItem("Manage Projects");
	         	Item.addActionListener(listener);
         		Item.setActionCommand("Projects"); 
         		
				popup.add(Item);
				
				Item = new MenuItem("Debug Window");
	         	Item.addActionListener(listener);
         		Item.setActionCommand("Debug"); 
         		
				popup.add(Item);
				
				Item = new MenuItem("Settings");
	         	Item.addActionListener(listener);
         		Item.setActionCommand("Settings"); 
         		
				popup.add(Item);
				popup.addSeparator();

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
		}//End Sync
	}
	
	public static void iconDestroy()
	{
		synchronized(lock_tray)
		{
			if(trayIcon != null)
			{
				SystemTray tray = SystemTray.getSystemTray(); //Get the tray
				tray.remove(trayIcon);
			}
		}//End Sync
	}
	
	/**
	 * 
	 * Creates Info Balloon at Tray Icon.
	 * 
	 * @param title Info Balloon Title
	 * @param msg Message to display
	 * @param type Icon to Display
	 * 
	 * @see net.sf.backcomp.utils.Tray
	 *
	 */
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
			try{
				if(e == null) return;
				
	            if(e.getActionCommand() == null) return;
	            
	            if( e.getActionCommand().equals("Quit") )
	            {
	            	Worker.terminate();
	            	//We know what plugins are loaded, terminate them in order.
	            	PluginLoader.VMHalt();
	            	Tray.iconDestroy();
	            	//throw new ThreadDeath();
	            }
	            else if( e.getActionCommand().equals("Projects") )
	            {
	            	PluginManager.show();
	            }          		
	            else if( e.getActionCommand().equals("Settings") )
	            {
	            	Settings.show();
				}
	            else if( e.getActionCommand().equals("Debug") )
	            {
	            	DebugDialog.show();
				}
			}
			catch(Exception ex)
			{
				Debug.messageDlg("Unhandled Exception in Tray Menu", DebugLevel.Fatal, ex);
				//ex.printStackTrace();
				//iconMessage( "Unhandled Exception in Tray Menu", ex.getMessage(), TrayIcon.MessageType.ERROR);				
			}
		}
	}
}
