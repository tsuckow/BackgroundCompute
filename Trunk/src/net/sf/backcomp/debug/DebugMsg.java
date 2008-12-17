/**
 * @(#)Debug.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
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
public final class DebugMsg
{
	private DebugLevel lvl = DebugLevel.Debug;
	private String msg = "";
	private String stack = null;//Stack Trace, if available
	private long time = 0;
	
	public DebugMsg( final String msg, final DebugLevel lvl )
	{
		this.msg = msg;
		this.lvl = lvl;
		time = new Date().getTime();
	}
	
	/**
	 * 
	 * @param msg Error Message
	 * @param lvl Debug level
	 * @param stack Stack trace
	 */
	public DebugMsg( final String msg, final DebugLevel lvl, final String stack )
	{
		this.msg = msg;
		this.lvl = lvl;
		this.stack = stack;
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
	
	public String getStack()
	{
		return stack;
	}
}
