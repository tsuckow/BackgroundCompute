/**
 * @(#)LangMan.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.utils;

import net.sf.backcomp.debug.*;

/**
 * 
 * Manages language localization.
 * 
 * @author Deathbob
 *
 */
public final class LangMan
{
	private LangMan(){}//This is a static class
	
	/**
	 * 
	 * @param key Resource Key
	 * @param alt Alternative if key is empty
	 * @return Localized value stored at {@code key} or {@code alt} if not found.
	 */
	public static String getString(String key, String alt)
	{
		if(BC.getLanguageBundle() != null)
		{
			try
			{
				return BC.getLanguageBundle().getString(key);
			}
			catch(Exception e)
			{
				//No such compatible key.
				Debug.message("Missing Locale Information For Key: " + key, DebugLevel.Information, e);
			}
		}
		return alt;
	}
	
}
