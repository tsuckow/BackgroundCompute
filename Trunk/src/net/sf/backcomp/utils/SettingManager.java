/**
 * @(#)SettingManager.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.utils;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;

/**
 * Provides a method of accessing the settings to the rest of the application without giving too much access.
 * 
 * @author Deathbob
 *
 */
public class SettingManager
{
	private SettingManager()
	{
	}//This is a static class
	
	public static String getSetting( final String key )
	{
		return BC.SETTINGS.getProperty( key );
	}
	
	public static String getDefaultSetting( final String key )
	{
		return BC.DEFAULT_SETTINGS.getProperty( key );
	}
	
	/**
	 * Sets a value to a Setting Key.
	 * 
	 * @param key Key to set
	 * @param value Value to place in the key.
	 * @return
	 */
	public static String setSetting( final String key, final String value )
	{
		String prop = null;
		try
		{
			prop = ( String ) BC.SETTINGS.setProperty( key, value );
		}
		catch ( final ClassCastException ex )
		{
			Debug.message( "Setting old value was not a string: " + key
				+ "\nNew Value: " + value, DebugLevel.Error, ex );
		}
		
		return prop;
	}
	
	/**
	 * Returns true if the Default Value for key will be returned by getSetting. Otherwise it returns false.
	 * 
	 * @param key Key to check in settings.
	 * @return
	 */
	public static boolean isDefaultSetting( final String key )
	{
		return !BC.SETTINGS.containsKey( key );
	}
	
	/**
	 * Erases a key and returns it to the Default.
	 * 
	 * @param key Key to revert to Default.
	 * @return
	 */
	public static String resetSetting( final String key )
	{
		String prop = null;
		try
		{
			prop = ( String ) BC.SETTINGS.remove( key );
		}
		catch ( final ClassCastException ex )
		{
			Debug.message(
				"Setting old value was not a string: " + key,
				DebugLevel.Error,
				ex );
		}
		
		return prop;
	}
}
