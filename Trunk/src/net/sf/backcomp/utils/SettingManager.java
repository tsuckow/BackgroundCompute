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
	private SettingManager(){}//This is a static class
	
	public static String getSetting(String key)
	{
		return BC.Settings.getProperty(key);
	}
	
	public static String getDefaultSetting(String key)
	{
		return BC.DefaultSettings.getProperty(key);
	}
	
	/**
	 * Sets a value to a Setting Key.
	 * 
	 * @param key Key to set
	 * @param value Value to place in the key.
	 * @return
	 */
	public static String setSetting(String key, String value)
	{
		String prop = null;
		try
		{
			prop = (String)BC.Settings.setProperty(key, value);
		}
		catch(ClassCastException ex)
		{
			Debug.message("Setting old value was not a string: " + key + "\nNew Value: " + value, DebugLevel.Error, ex);
		}
		
		return prop;
	}
	
	/**
	 * Returns true if the Default Value for key will be returned by getSetting. Otherwise it returns false.
	 * 
	 * @param key Key to check in settings.
	 * @return
	 */
	public static boolean isDefaultSetting(String key)
	{
		return !BC.Settings.containsKey(key);
	}
	
	/**
	 * Erases a key and returns it to the Default.
	 * 
	 * @param key Key to revert to Default.
	 * @return
	 */
	public static String resetSetting(String key)
	{
		String prop = null;
		try
		{
			prop = (String)BC.Settings.remove(key);
		}
		catch(ClassCastException ex)
		{
			Debug.message("Setting old value was not a string: " + key, DebugLevel.Error, ex);
		}
		
		return prop;
	}
}
