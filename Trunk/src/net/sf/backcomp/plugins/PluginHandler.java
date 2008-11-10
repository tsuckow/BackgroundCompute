package net.sf.backcomp.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JPanel;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;

/**
 * Serves as a middle man between a plugin and the main application.
 * @author Deathbob
 *
 */
public class PluginHandler
{
	final String pluginName;
	private Plugin myPlugin = null;
	private PluginInterconnect myInterconnect = null;
	private int cores = 0;
	private boolean loaded = false;
	
	/**
	 * Loads an instance of a plugin.
	 * @param p File name of string
	 */
	PluginHandler(String p)
	{
		pluginName = p;
		
		URLClassLoader UCL = null;
		
		Class<?> loadedClass = null;
		
		Plugin loadedPlugin = null;
		
		//Setup the loader.
		try
		{
			UCL = new URLClassLoader(new URL[]{new File("plugins/" + pluginName + "/").toURI().toURL()});
		}
		catch(MalformedURLException ex)
		{
			Debug.message("Plugin Dir Path Malformed! " + "plugins/" + pluginName + "/",DebugLevel.Error);
			return;
		}
		
		//Load the Class
		try
		{
			loadedClass = UCL.loadClass(pluginName);
		}
		catch(ClassNotFoundException ex)
		{
			Debug.message("Class not found: " + "plugins/" + pluginName + "/" + pluginName,DebugLevel.Error,ex);
			return;
		}
		catch(NoClassDefFoundError ex)
		{
			Debug.message("Class Definition not found: " + "plugins/" + pluginName + "/" + pluginName,DebugLevel.Error,ex);
			return;
		}
		
		//Is a Plugin?
		if( !Plugin.class.isAssignableFrom(loadedClass) )
		{
			Debug.message("Class not a plugin: " + pluginName,DebugLevel.Error);
			return;
		}
		
		
		try
		{	
			loadedPlugin = (Plugin)loadedClass.newInstance();
		}
		catch(InstantiationException ex)
		{
			Debug.message("Failed to load Plugin: " + pluginName,DebugLevel.Error);
			return;
		}
		catch(IllegalAccessException ex)
		{
			Debug.message("Illegal Access to Plugin: " + pluginName,DebugLevel.Error);
			return;
		}
		catch(NoClassDefFoundError ex)
		{
			Debug.message("Class Def Missing: " + pluginName,DebugLevel.Error,ex);
			return;
		}
		
		myPlugin = loadedPlugin;
		
		myInterconnect = new PluginInterconnect();
		myPlugin.initialize(myInterconnect);
		
		loaded = true;
	}
	
	public void stop()
	{
		if( !isValid() ) return;
		myPlugin.halt();
	}
	
	public void start()
	{
		if( !isValid() ) return;
		myPlugin.start();
	}
	
	/**
	 * Returns if the plugin is active.<br>
	 * A plugin is active if it is in the Running or Paused states.
	 * 
	 * @return Active or not
	 */
	public boolean isActive()
	{
		if( !isValid() ) return false;
		PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
		return
			   ps == PluginInterconnect.PluginState.Running
			|| ps == PluginInterconnect.PluginState.Paused;
	}
	
	public boolean isPaused()
	{
		if( !isValid() ) return false;
		PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
		return ps == PluginInterconnect.PluginState.Paused;
	}
	
	public boolean isStopped()
	{
		if( !isValid() ) return true;
		PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
		return
			   ps == PluginInterconnect.PluginState.Stopped
			|| ps == PluginInterconnect.PluginState.NeedReload;
	}
	
	public void uninstall()
	{
		if( !isValid() ) return;
		myPlugin.uninstall();
	}
	
	public String getName()
	{
		if( !isValid() ) return "";
		return myPlugin.getName();
	}
	
	public String getInfo()
	{
		if( !isValid() ) return null;
		return myPlugin.getInfo();
	}
	
	public JPanel getStatus()
	{
		if( !isValid() ) return null;
		return myPlugin.getStatus();
	}
	
	public boolean isValid()
	{
		if(loaded && myInterconnect != null)
		{
			PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
			
			if(ps == PluginInterconnect.PluginState.NeedReload)
			{
				loaded = false;
				myPlugin = null;
				myInterconnect = null;
				return false;
			}
			
			if(myPlugin==null || myInterconnect == null) return false;
			
			return
				   ps != PluginInterconnect.PluginState.Removed
				&& ps != PluginInterconnect.PluginState.Initilizing;
		}
		return false;
		
	}
	
	public void unLoad()
	{
		stop();
		loaded = false;
		myPlugin = null;
		myInterconnect = null;
	}
	
	public int getRunningCores()
	{
		return isActive()?cores:0;
	}
	
	public void stopCore()
	{
		if( !isValid() ) return;
		if(cores > 0) --cores;
	}
	
	public void startCore()
	{
		if( !isValid() ) return;
		if( !isActive() ) myPlugin.start();
		++cores;
	}
	
	public int wantedCores()
	{
		if( !isValid() ) return -1;
		return myInterconnect.getWantedCpu();
	}
}
