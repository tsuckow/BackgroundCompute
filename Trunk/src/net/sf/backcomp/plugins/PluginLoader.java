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

/**
 * Loads plugins and maintains the list of valid ones.
 * @author Deathbob
 *
 */
public final class PluginLoader
{
	private static long lastCheckTime = 0;
	
	private PluginLoader()
	{
	}//This is a static class
	
	/**
	 * The cache of the plugins.
	 */
	private static volatile HashMap< String, PluginHandler > PluginCache =
		new HashMap< String, PluginHandler >();
	
	/**
	 * 
	 * Loads a Plugin located in the "plugin" subdirectory.
	 * 
	 * @param name Name of plugin to load or null on failure.
	 * @return instance of Plugin given by name. 
	 */
	public static PluginHandler loadPlugin( final String name )
	{
		if ( name == null )
		{
			return null;
		}
		
		//Get the one in the cache
		PluginHandler plugin = PluginCache.get( name );
		
		//Found it
		if ( plugin != null )
		{
			if ( plugin.isValid() )
			{
				return plugin;
			}
			else
			{
				PluginCache.remove( name );
				return null;
			}
		}
		
		//Didn't Find it.
		plugin = new PluginHandler( name );
		if ( plugin.isValid() )
		{
			PluginCache.put( name, plugin );
			return plugin;
		}
		else
		{
			return null;
		}
	}
	
	//TODO: Add a way to guarantee it will not load new plugins (for like when the app is closing)
	/**
	 * 
	 * Returns name of classes loaded of Type Plugin.<br>
	 * Once a minute it looks for new plugins.
	 * 
	 * @return array of Plugin names that are installed.
	 */
	public static String[] getLoadedPlugins()
	{
		if ( lastCheckTime + 100 * 60 < System.currentTimeMillis() )
		{
			findPlugins();
			lastCheckTime = System.currentTimeMillis();
		}
		return PluginCache.keySet().toArray( new String[0] );
	}
	
	/**
	 * Attempts to mount every installed plugin.
	 */
	public static void findPlugins()
	{
		final String Dir = "plugins/";
		
		final FilenameFilter filter = new FilenameFilter()
			{
				public boolean accept( final File dir, final String name )
				{
					final File pdir = new File( dir, name );
					if ( pdir.isDirectory() )
					{
						loadPlugin( name ); //Try to mount and cache.
					}
					
					return false;
				}
			};
		
		final File src = new File( Dir );
		src.listFiles( filter ); //We discard the result since we dont need it
	}
	
	/**
	 * Halts all plugins when the application is closing.
	 */
	public static void VMHalt()
	{
		for ( final String plug : getLoadedPlugins() )
		{
			final PluginHandler ph = loadPlugin( plug );
			if ( ph != null )
			{
				ph.unLoad();
			}
		}
	}
}
