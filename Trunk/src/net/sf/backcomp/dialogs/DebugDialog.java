/**
 * @(#)DebugDialog.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import javax.swing.*;

public final class DebugDialog
{
	private DebugDialog(){}
	
	protected static JFrame frame = null;
	
	/**
	 * Handles Initilizing the frame.
	 */
	public final static void show()
	{
		if(frame!=null)
		{
			if(frame.isShowing())
			{
				frame.toFront();
				return;
			}
			frame.dispose();
			frame = null;
		}
		frame = new JFrame("Debug Window");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		populate();
		
		if(frame != null) frame.setVisible(true);
	}
	
	/**
	 * Populates the frame.
	 */
	protected static void populate()
	{
		frame.add(new JLabel("Test"));
	}
}
