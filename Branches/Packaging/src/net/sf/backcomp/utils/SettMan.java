/**
 * @(#)SettMan.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.utils;

public class SettMan
{
	private SettMan(){}//This is a static class
	
	public static String getSetting(String key)
	{
		return BC.Settings.getProperty(key);
	}
	
	public static boolean setSetting(String key, String value)
	{
		return true;
	}
}
