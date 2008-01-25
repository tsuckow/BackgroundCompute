/**
 * @(#)Mainapp.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.utils;

final class Mainapp implements Runnable {
	public void run() {
		Tray.iconCreate();

		Worker WorkT = new Worker();
		WorkT.start();
	}
}