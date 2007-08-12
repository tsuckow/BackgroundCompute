/**
 * @(#)Utils.java
 *
 *
 * @author 
 * @version 1.00 2007/2/11
 */
 
import java.awt.*;
import java.awt.event.*;

import java.net.*; //URL
import java.io.*;//File

/**
 * 
 * Utilities for Background Compute
 * 
 * @author Deathbob
 * 
 * @version 0.1
 * 
 *
 */
public class Utils
{
	private Utils(){} //Since this only contains static members, prevent an instance of this class from being created.  
	
	//Locks
	private final static Object lock_tray = new int[1];//Create a REALLY SMALL but unique object.
	
	private static TrayIcon trayIcon = null;
	
	//TODO: Make tray icon subclass
	
	/**
	 * 
	 * Creates a Tray Icon if one does not already exist.
	 * 
	 * @author Deathbob
	 * 
	 */
	//TODO: Make sure only one instance
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
         		Item.setActionCommand("AddPlugin"); 
         		
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
			//TODO: Remove this line
			iconMessage("Tray Icon","This was created in utils",TrayIcon.MessageType.INFO);
		}
	}
	
	/**
	 * 
	 * Creates Info Balloon at Tray Icon.
	 * 
	 * @author Deathbob
	 * 
	 * @param title Info Balloon Title
	 * @param msg Message to display
	 * @param type Icon to Display
	 * 
	 * @see Utils
	 *
	 */
	public static void iconMessage(String title, String msg, TrayIcon.MessageType type)
	{
		synchronized(lock_tray)
		{
			trayIcon.displayMessage(title,msg,type);
		}
	}
	
	/**
	 * 
	 * Loads a Plugin located in the "plugin" subdirectory.
	 * 
	 * @author Deathbob 
	 * @param name Name of plugin to load or null on failure.
	 * @return instance of Plugin given by name. 
	 */
	//TODO: exception handling needs work.
	public static Plugin loadPlugin(String name)
	{
		if(name == null) return null;
		
		ClassLoader CL = BC.class.getClassLoader();
		
		URLClassLoader UCL = null;
		
		Plugin test = null;
		
		try
		{
			UCL = new URLClassLoader(new URL[]{new File("plugins/" + name + "/").toURI().toURL()},CL);
		}
		catch(MalformedURLException ex)
		{
			BC.PError("Plugin Dir Path Malformed!");
			return null;
		}
		try
		{
			
			test = (Plugin)UCL.loadClass(name + "_Plugin").newInstance();
			
		}
		catch(ClassNotFoundException ex)
		{
			BC.PError("Class not found: " + "plugins/" + name + "/" + name + "_Plugin");
			return null;
		}
		catch(InstantiationException ex)
		{
			BC.PError("Failed to load class");
			return null;
		}
		catch(IllegalAccessException ex)
		{
			BC.PError("Illegal Access");
			return null;
		}
		catch(NoClassDefFoundError ex)
		{
			BC.PError("Class File Corrupted");
			return null;
		}	
		
		return test;
	}
	
	private static class iconListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
        {
			try{
			if(e == null) return;
            if(e.getActionCommand() == null) return;
            if( e.getActionCommand().equals("Quit") )
            	System.exit(0);
            if( e.getActionCommand().equals("Plugins") )
            {
            	PluginManager.show();
            }
            
            if( e.getActionCommand().equals("AddPlugin") )
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
			catch(Exception ex)
			{
				iconMessage( ex.getMessage() ,"Unhandled Exception", TrayIcon.MessageType.ERROR);				
			}
		}
	}
	
	/**
	 * 
	 * Attempts to mount every installed plugin and returns name of classes of Type Plugin.
	 * 
	 * @author Deathbob
	 * @return array of Plugin names that are installed.
	 */
	public static String[] getLocalPlugins()
	{
		String Dir = "plugins/";
		
		File[] files = null;
		FilenameFilter filter = new FilenameFilter(){
        	public boolean accept(File dir, String name)
        	{
        		File pdir = new File(dir,name);
            	if( pdir.isDirectory() )
            	{
            		if(Utils.loadPlugin(name)!=null)
            		{
            			return true;
            		}
            	}
            	
            	return false;
        	}
    	};
    	
    	File src = new File(Dir);
    	files = src.listFiles(filter);
    	
    	String[] plugins = new String[files.length];
    	for(int i = 0; i < files.length; ++i)
    	{
    		plugins[i] = files[i].getName();
    	}
    	
    	return plugins;
	} 
}