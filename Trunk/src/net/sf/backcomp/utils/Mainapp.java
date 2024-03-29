/**
 * @(#)Mainapp.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.utils;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;

final class Mainapp implements Runnable
{
	public void run()
	{
		Thread
			.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler()
				{
					//Implements Thread.UncaughtExceptionHandler.uncaughtException()
					public void uncaughtException(
						final Thread th,
						final Throwable ex )
					{
						try
						{
							Debug.messageDlg( "You crashed thread "
								+ th.getName(), DebugLevel.Fatal, ex );
						}
						catch ( final Throwable t )
						{
							System.err.println( "Oh Shit. Debuging Failed!\n"
								+ t.toString() + "\n" + ex.toString() + "\n\n" );
						}
					}
				} );
		
		Tray.iconCreate();
		
		//Add test Debug Messages
		Debug.message( "Test Fatal", DebugLevel.Fatal );
		Debug.message( "Test Error", DebugLevel.Error );
		Debug.message( "Test Warning", DebugLevel.Warning );
		Debug.message( "Test Information", DebugLevel.Information );
		Debug.message( "Test Not Implemented", DebugLevel.NotImplemented );
		Debug.message( "Test Debug", DebugLevel.Debug );
		
		final Worker WorkT = new Worker();
		WorkT.start();
	}
}