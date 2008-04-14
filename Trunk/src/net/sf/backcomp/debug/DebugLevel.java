/**
 * @(#)DebugLevel.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.debug;

import java.awt.*;

public enum DebugLevel
{	//Lvl			Name				DarkColor					LightColor					ForeColor
	Fatal			("Fatal",			new Color(0x66,0x00,0x00),	new Color(0xFF,0x00,0x00),	new Color(0xEE,0xEE,0xEE)),
	Error			("Error",			new Color(0xFF,0x66,0x00),	new Color(0xFF,0x99,0x00),	Color.BLACK),
	Warning			("Warning",			Color.YELLOW,				new Color(0xFF,0xFF,0xBB),	Color.BLACK),
	Information		("Information",		new Color(0x00,0xCC,0xFF),	Color.CYAN,					Color.BLACK),
	NotImplemented	("Not Implemented", new Color(0x44,0x44,0x44),	new Color(0x66,0x66,0x66),	new Color(0xEE,0xEE,0xEE)),
	Debug			("Debug",			null,						null,						null);
	
	private final String name;
	private Color darkColor;
	private Color lightColor;
	private Color foreColor;
	
	
	private DebugLevel(String name, Color darkColor, Color lightColor, Color foreColor)
	{
		this.name = name;
		this.darkColor = darkColor;
		this.lightColor = lightColor;
		this.foreColor = foreColor;
	}
	
	/**
	 * Returns the human readable short name of the Level
	 */
	@Override
	public String toString()
	{
		return name;
	}
	
	public Color getDarkColor()
	{
		return darkColor;
	}
	
	public Color getLightColor()
	{
		return lightColor;
	}
	
	public Color getForeColor()
	{
		return foreColor;
	}
}