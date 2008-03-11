/**
 * @(#)Debug.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.debug;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;

public final class Debug
{
	final static String NEW_LINE = System.getProperty("line.separator");
	private Debug(){}//This is a static class
	
	private static CopyOnWriteArrayList<DebugMsg> Msgs = new CopyOnWriteArrayList<DebugMsg>();
	
	public static void message(String msg, DebugLevel lvl)
	{
		//Get Time, Add to ArrayList
		DebugMsg dm = new DebugMsg(msg,lvl);
		Msgs.add(dm);
		if(Msgs.size() > 1000) Msgs.remove(0);
	}
	
	public static void messageDlg(String msg, DebugLevel lvl)
	{
		message(msg, lvl);
		
		int icon = 0;
		switch(lvl)
		{
			case NotImplemented:
			case Error:
				icon = JOptionPane.ERROR_MESSAGE;
				break;
			case Warning:
				icon = JOptionPane.WARNING_MESSAGE;
				break;
			case Information:
				icon = JOptionPane.INFORMATION_MESSAGE;
				break;
			case Debug:
				icon = JOptionPane.QUESTION_MESSAGE;
				break;
			default:
				icon = JOptionPane.PLAIN_MESSAGE;
				break;
		}	
		JOptionPane.showMessageDialog(null,msg,"Debug Level: " + lvl.toString(),icon);
	}
	
	public static void message(String msg, DebugLevel lvl, Throwable thrown)
	{
		String stack = getStackTrace(thrown);
		
		//Get Time, Add to ArrayList
		DebugMsg dm = new DebugMsg(msg,lvl,stack);
		Msgs.add(dm);
	}
	
	public static void messageDlg(String msg, DebugLevel lvl, Throwable thrown)
	{
		message(msg, lvl, thrown);
		
		String stack = getStackTrace(thrown);
		
		int icon = 0;
		switch(lvl)
		{
			case Fatal:
			case Error:
				icon = JOptionPane.ERROR_MESSAGE;
				break;
			case NotImplemented:
			case Warning:
				icon = JOptionPane.WARNING_MESSAGE;
				break;
			case Information:
				icon = JOptionPane.INFORMATION_MESSAGE;
				break;
			case Debug:
				icon = JOptionPane.QUESTION_MESSAGE;
				break;
			default:
				icon = JOptionPane.PLAIN_MESSAGE;
				break;
		}	
		JOptionPane.showMessageDialog(null,msg + NEW_LINE + NEW_LINE + stack,"Debug Level: " + lvl.toString(),icon);
	}
	
	/**
	 *
	 * @param aThrowable Throwable object to generate stack trace from
	 * @return "Stack Trace: <code>stacktrace</code>"
	 */
	public static String getStackTrace(Throwable aThrowable)
	{
	    //add the class name and any message passed to constructor
	    final StringBuilder result = new StringBuilder( "Stack Trace: " );
	    result.append(aThrowable.toString());
	    
	    result.append(NEW_LINE);

	    //add each element of the stack trace
	    for (StackTraceElement element : aThrowable.getStackTrace() ){
	      result.append( element );
	      result.append( NEW_LINE );
	    }
	    return result.toString();
	}
	
	/**
	 * Returns an iterator of the current messages. This iterator is guaranteed not to throw ConcurrentModificationException.
	 * @return Iterator of all current messages
	 */
	public static Iterator<DebugMsg> getIterator()
	{
		return Msgs.iterator();
	}
	
	/**
	 * Returns an array of the current messages.
	 * @return Array of all current messages
	 */
	public static DebugMsg[] getArray()
	{
		return Msgs.toArray(new DebugMsg[0]);
	}
}
