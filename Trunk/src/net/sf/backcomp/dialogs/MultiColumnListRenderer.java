/**
 * @(#)MultiColumnListRenderer.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;
import net.sf.backcomp.plugins.PluginHandler;
import net.sf.backcomp.plugins.PluginLoader;

class MultiColumnListRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 1L;
	String[] status = new String[4];
	
	public MultiColumnListRenderer()
	{
		status[0] = "images/status_green.png";
		status[1] = "images/status_red.png";
		status[2] = "images/status_blue.png";
		status[3] = "images/status_orange.png";
	}
	
	private int stateCode( final PluginHandler pm )
	{
		if ( pm.isActive() )
		{
			if ( pm.isPaused() )
			{
				return 3;
			}
			else
			{
				return 0;
			}
		}
		else if ( pm.isStopped() )
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}
	
	/*
	 * This method finds the image and text corresponding
	 * to the selected value and returns the label, set up
	 * to display the text and image.
	 */
	@Override
	public Component getListCellRendererComponent(
		final JList list,
		final Object value,
		final int index,
		final boolean isSelected,
		final boolean cellHasFocus )
	{
		
		final JPanel wow = new JPanel();
		try
		{
			wow.setLayout( new BorderLayout() );
			
			final PluginHandler plug =
				PluginLoader.loadPlugin( ( String ) value );
			
			int state = 2;
			String name = "Plugin No Longer Exists";
			
			if ( plug != null )
			{
				state = stateCode( plug );
				name = plug.getName();
			}
			
			final String image =
				ClassLoader.getSystemResource( status[state] ).toString();
			
			final JLabel a =
				new JLabel(
					"<html><table style='border-style: solid; border-width: 1pt 1pt 1pt 1pt; width:146pt;'><tr><td>"
						+ name
						+ "</td><td style='text-align:right;'><img src='"
						+ image + "'></td></tr></table></html>" );
			
			wow.add( a, BorderLayout.CENTER );
			
			/*renderer.*/wow.setBackground( isSelected ? list
				.getSelectionBackground() : null );
			/*renderer.*/wow.setForeground( isSelected ? list
				.getSelectionForeground() : null );
			
		}
		catch ( final Exception ex )
		{
			Debug.messageDlg(
				"Unhandled Exception in Plugin Manager List Renderer",
				DebugLevel.Error,
				ex );
		}
		
		return wow;
	}
	
}