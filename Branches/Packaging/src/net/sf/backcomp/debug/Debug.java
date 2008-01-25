/**
 * @(#)Debug.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.debug;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;

public final class Debug
{
	private Debug(){}//This is a static class
	
	private static CopyOnWriteArrayList<DebugMsg> Msgs = new CopyOnWriteArrayList<DebugMsg>();
	
	public static void message(String msg, DebugLevel lvl)
	{
		//Get Time, Add to ArrayList
		DebugMsg dm = new DebugMsg(msg,lvl);
		Msgs.add(dm);
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
	
	//TODO: Add this.
	/*public static String getStackTrace(Throwable aThrowable) {
	    //add the class name and any message passed to constructor
	    final StringBuilder result = new StringBuilder( "ERROR: " );
	    result.append(aThrowable.toString());
	    final String NEW_LINE = System.getProperty("line.separator");
	    result.append(NEW_LINE);

	    //add each element of the stack trace
	    for (StackTraceElement element : aThrowable.getStackTrace() ){
	      result.append( element );
	      result.append( NEW_LINE );
	    }
	    return result.toString();
	  }*/
}
