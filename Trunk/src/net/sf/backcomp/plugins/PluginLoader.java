/**
 * @(#)PluginLoader.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.plugins;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

//FIXME: Who knows what this does.

public final class PluginLoader
{
	private PluginLoader(){}//This is a static class
	
	/**
	 * The cache of the plugins.
	 */
	private static HashMap<String,PluginHandler> PluginCache = new HashMap<String,PluginHandler>();
	//TODO: Cache need to expire and have ability to unload/refresh plugins. (Bool flag on load that will cause it to dump, GC, GC and load again? [This would require syncronizing.])
	
	/**
	 * 
	 * Loads a Plugin located in the "plugin" subdirectory.
	 * 
	 * @param name Name of plugin to load or null on failure.
	 * @return instance of Plugin given by name. 
	 */
	//TODO: exception handling needs work.
	public static PluginHandler loadPlugin(String name)
	{
		if(name == null) return null;
		
		//Get the one in the cache
		PluginHandler plugin = PluginCache.get(name);
		
		//Found it
		if(plugin != null)
		{
			if( plugin.isValid() )
			{
				return plugin;
			}
			else
			{
				PluginCache.remove(name);
				return null;
			}
		}
		
		//Didn't Find it.
		plugin = new PluginHandler(name);
		if( plugin.isValid() )
		{
			PluginCache.put(name, plugin);
			return plugin;
		}
		else
			return null;
	}
	
	
	
	/**
	 * 
	 * Attempts to mount every installed plugin and returns name of classes of Type Plugin.
	 * 
	 * @return array of Plugin names that are installed.
	 */
	public static String[] getLoadedPlugins()
	{
		return PluginCache.keySet().toArray(new String[0]);
	}
	
	/**
	 * Attempts to mount every installed plugin.
	 * 
	 */
	public static void findPlugins()
	{
		String Dir = "plugins/";
		
		FilenameFilter filter = new FilenameFilter(){
        	public boolean accept(File dir, String name)
        	{
        		File pdir = new File(dir,name);
            	if( pdir.isDirectory() )
            	{
            		loadPlugin(name); //Try to mount and cache.
            	}
            	
            	return false;
        	}
    	};
    	
    	File src = new File(Dir);
    	src.listFiles(filter); //We discard the result since we dont need it
	}
	
	public static void VMHalt()
	{
		for( String plug : getLoadedPlugins() )
		{
			PluginHandler ph = loadPlugin(plug);
			if( ph != null ) ph.unLoad();
		}
	}
}
