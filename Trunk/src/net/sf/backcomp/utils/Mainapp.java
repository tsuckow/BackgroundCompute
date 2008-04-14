/**
 * @(#)Mainapp.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.utils;

import net.sf.backcomp.debug.*;

final class Mainapp implements Runnable {
	public void run() {
		Tray.iconCreate();

		//Add test Debug Messages
		Debug.message("Test Fatal", DebugLevel.Fatal);
		Debug.message("Test Error", DebugLevel.Error);
		Debug.message("Test Warning", DebugLevel.Warning);
		Debug.message("Test Information", DebugLevel.Information);
		Debug.message("Test Not Implemented", DebugLevel.NotImplemented);
		Debug.message("Test Debug", DebugLevel.Debug);
		
		Worker WorkT = new Worker();
		WorkT.start();
	}
}