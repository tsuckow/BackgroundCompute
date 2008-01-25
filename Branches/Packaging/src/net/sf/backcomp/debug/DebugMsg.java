/**
 * @(#)Debug.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.debug;

import java.util.Date;

/**
 * 
 * A debugging message.
 * 
 * @author Deathbob
 *
 */
final class DebugMsg
{
	private DebugLevel lvl = DebugLevel.Debug;
	private String msg = "";
	private long time = 0;
	
	public DebugMsg(String msg, DebugLevel lvl)
	{
		this.msg = msg;
		this.lvl = lvl;
		time = new Date().getTime();
	}
	
	public String getMsg()
	{
		return msg;
	}
	
	public DebugLevel getLevel()
	{
		return lvl;
	}
	
	public long getTime()
	{
		return time;
	}
}
