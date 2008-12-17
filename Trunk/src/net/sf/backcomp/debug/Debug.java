/**
 * @(#)Debug.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.debug;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.backcomp.dialogs.DebugEventDialog;

public final class Debug
{
	final static String NEW_LINE = System.getProperty( "line.separator" );
	
	private Debug()
	{
	}//This is a static class
	
	private static CopyOnWriteArrayList< DebugMsg > Msgs =
		new CopyOnWriteArrayList< DebugMsg >();
	
	public static DebugMsg message( final String msg, final DebugLevel lvl )
	{
		//Get Time, Add to ArrayList
		final DebugMsg dm = new DebugMsg( msg, lvl );
		Msgs.add( dm );
		if ( Msgs.size() > 500 )
		{
			Msgs.remove( 0 );
		}
		return dm;
	}
	
	public static DebugMsg message(
		final String msg,
		final DebugLevel lvl,
		final Throwable thrown )
	{
		final String stack = getStackTrace( thrown );
		
		//Get Time, Add to ArrayList
		final DebugMsg dm = new DebugMsg( msg, lvl, stack );
		Msgs.add( dm );
		if ( Msgs.size() > 500 )
		{
			Msgs.remove( 0 );
		}
		return dm;
	}
	
	public static void messageDlg( final String msg, final DebugLevel lvl )
	{
		final DebugMsg dm = message( msg, lvl );
		
		new DebugEventDialog( null, dm );
	}
	
	public static void messageDlg(
		final String msg,
		final DebugLevel lvl,
		final Throwable thrown )
	{
		final DebugMsg dm = message( msg, lvl, thrown );
		
		new DebugEventDialog( null, dm );
	}
	
	/**
	 *
	 * @param aThrowable Throwable object to generate stack trace from
	 * @return "Stack Trace: <code>stacktrace</code>"
	 */
	public static String getStackTrace( final Throwable aThrowable )
	{
		//add the class name and any message passed to constructor
		final StringBuilder result = new StringBuilder();
		result.append( aThrowable.toString() );
		
		result.append( NEW_LINE );
		
		//add each element of the stack trace
		for ( final StackTraceElement element : aThrowable.getStackTrace() )
		{
			result.append( element );
			result.append( NEW_LINE );
		}
		return result.toString();
	}
	
	/**
	 * Returns an iterator of the current messages. This iterator is guaranteed not to throw ConcurrentModificationException.
	 * @return Iterator of all current messages
	 */
	public static Iterator< DebugMsg > getIterator()
	{
		return Msgs.iterator();
	}
	
	/**
	 * Returns an array of the current messages.
	 * @return Array of all current messages
	 */
	public static DebugMsg[] getArray()
	{
		return Msgs.toArray( new DebugMsg[0] );
	}
}
