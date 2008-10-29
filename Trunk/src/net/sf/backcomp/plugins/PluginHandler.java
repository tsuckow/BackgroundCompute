package net.sf.backcomp.plugins;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;

import javax.swing.JPanel;

import net.sf.backcomp.Exceptions.NotImplementedException;
import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;

/*
 * ClassLoader CL = net.sf.backcomp.utils.BC.class.getClassLoader();
		
		URLClassLoader UCL = null;
		
		PluginHandler test = null;
		
		//Load from cache if available
		test = PluginCache.get(name + "_Plugin");
		
		if(test != null)
		{
			//Checks if this class is reloading and needs cache clear.
			if(test.needReload())
			{
				PluginCache.remove(name + "_Plugin");
				test = null;//Remove our reference.
				return null;
			}
			return test;
		}
		
		try
		{
			//FIXME: Shouldn't this second one be null. Except what about the bootstrap loader being null.
			UCL = new URLClassLoader(new URL[]{new File("plugins/" + name + "/").toURI().toURL()},CL);
		}
		catch(MalformedURLException ex)
		{
			Debug.message("Plugin Dir Path Malformed!",DebugLevel.Error);
			return null;
		}
		try
		{
			
			test = (Plugin)UCL.loadClass(name + "_Plugin").newInstance();
			
		}//TODO:IMprove the error messages
		catch(ClassNotFoundException ex)
		{
			Debug.message("Class not found: " + "plugins/" + name + "/" + name + "_Plugin",DebugLevel.Error);
			return null;
		}
		catch(InstantiationException ex)
		{
			Debug.message("Failed to load class",DebugLevel.Error);
			return null;
		}
		catch(IllegalAccessException ex)
		{
			Debug.message("Illegal Access",DebugLevel.Error);
			return null;
		}
		catch(NoClassDefFoundError ex)
		{
			Debug.message("Class File Corrupted",DebugLevel.Error);
			return null;
		}
		
		//Checks if this class is reloading
		if(test.needReload())
		{
			test = null;//Remove our reference.
			return null;
		}
		
		
		
		PluginCache.put(name + "_Plugin", test);
		return test;
 */

public class PluginHandler
{
	final String pluginName;
	private Plugin myPlugin = null;
	private PluginInterconnect myInterconnect = null;
	private int cores = 0;
	private boolean loaded = false;
	
	PluginHandler(String p)
	{
		pluginName = p;
		
		ClassLoader CL = net.sf.backcomp.utils.BC.class.getClassLoader();
		
		URLClassLoader UCL = null;
		
		Class<?> loadedClass = null;
		
		Plugin loadedPlugin = null;
		
		//Setup the loader.
		try
		{
			//FIXME: Shouldn't this second one be null. Except what about the bootstrap loader being null.
			UCL = new URLClassLoader(new URL[]{new File("plugins/" + pluginName + "/").toURI().toURL()},CL);
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
			Debug.message("Class not found: " + "plugins/" + pluginName + "/" + pluginName,DebugLevel.Error);
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
			Debug.message("Class Def Missing: " + pluginName,DebugLevel.Error);
			return;
		}
		
		myPlugin = loadedPlugin;
		
		myInterconnect = new PluginInterconnect();
		myPlugin.initialize(myInterconnect);
		
		loaded = true;
	}
	
	public void stop()
	{
		//FIXME: Initilized?
		myPlugin.halt();
		myPlugin = null;
	}
	
	/**
	 * Returns if the plugin is active.<br>
	 * A plugin is active if it is in the Running or Paused states.
	 * 
	 * @return Active or not
	 */
	public boolean isActive()
	{
		//FIXME: Initilized?
		PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
		return
			   ps == PluginInterconnect.PluginState.Running
			|| ps == PluginInterconnect.PluginState.Paused;
	}
	
	public boolean isPaused()
	{
		//FIXME: Initilized?
		PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
		return ps == PluginInterconnect.PluginState.Paused;
	}
	
	public boolean isStopped()
	{
		//FIXME: Initilized?
		PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
		return
			   ps == PluginInterconnect.PluginState.Stopped
			|| ps == PluginInterconnect.PluginState.NeedReload;
	}
	
	public void uninstall()
	{
		//FIXME: Initilized?
		myPlugin.uninstall();
	}
	
	public String getName()
	{
		//FIXME: INit?
		return myPlugin.getName();
	}
	
	public String getInfo()
	{
		//FIXME: INit?
		return myPlugin.getInfo();
	}
	
	public JPanel getStatus()
	{
		return myPlugin.getStatus();
	}
	
	public boolean isValid()
	{
		if(!loaded) return false;
		PluginInterconnect.PluginState ps = myInterconnect.getPluginState();
		return
			   ps != PluginInterconnect.PluginState.Removed
			&& ps != PluginInterconnect.PluginState.Initilizing;
	}
	
	public void unLoad()
	{
		stop();
		//FIXME: Unload
	}
	
	public int getRunningCores()
	{
		return cores;
	}
	
	public void stopCore()
	{
		if(cores > 0) --cores;
	}
	
	public void startCore()
	{
		++cores;
	}
}
