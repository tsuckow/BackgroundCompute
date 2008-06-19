/**
 * @(#)PluginLoader.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.plugins;

import net.sf.backcomp.debug.*;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public final class PluginLoader
{
	private PluginLoader(){}//This is a static class
	
	/**
	 * The cache of the plugins.
	 */
	private static HashMap<String,Plugin> PluginCache = new HashMap<String,Plugin>();
	//TODO: Cache need to expire and have ability to unload/refresh plugins. (Bool flag on load that will cause it to dump, GC, GC and load again? [This would require syncronizing.])
	
	/**
	 * 
	 * Loads a Plugin located in the "plugin" subdirectory.
	 * 
	 * @param name Name of plugin to load or null on failure.
	 * @return instance of Plugin given by name. 
	 */
	//TODO: exception handling needs work.
	public static Plugin loadPlugin(String name)
	{
		
		if(name == null) return null;
		
		ClassLoader CL = net.sf.backcomp.utils.BC.class.getClassLoader();
		
		URLClassLoader UCL = null;
		
		Plugin test = null;
		
		//Load from cache if availible
		test = PluginCache.get(name + "_Plugin");
		
		if(test != null)
		{
			//Checks if this class is reloading and forces Garbage Collection
			//This is usually not recommended but this should not happen often.
			if(test.needReload())
			{
				PluginCache.remove(name + "_Plugin");
				test = null;//Remove our referance.
				System.gc();
				try
		    	{
					Thread.sleep(100);
		    	}
		    	catch(InterruptedException e){}
				System.gc();
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
		
		//Checks if this class is reloading and forces Garbage Collection
		//This is usually not recommended but this should not happen often.
		if(test.needReload())
		{
			test = null;//Remove our referance.
			System.gc();
			try
	    	{
				Thread.sleep(100);
	    	}
	    	catch(InterruptedException e){}
			System.gc();
			return null;
		}
		
		PluginCache.put(name + "_Plugin", test);
		return test;
	}
	
	
	
	/**
	 * 
	 * Attempts to mount every installed plugin and returns name of classes of Type Plugin.
	 * 
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
            		if(loadPlugin(name)!=null)
            		{
            			return true;
            		}
            	}
            	
            	return false;
        	}
    	};
    	
    	File src = new File(Dir);
    	files = src.listFiles(filter);
    	
    	//
    	//FIXME:Null pointer exception when no plugins. files = null.
    	String[] plugins = new String[files.length];
    	for(int i = 0; i < files.length; ++i)
    	{
    		plugins[i] = files[i].getName();
    	}
    	
    	return plugins;
	}
}
