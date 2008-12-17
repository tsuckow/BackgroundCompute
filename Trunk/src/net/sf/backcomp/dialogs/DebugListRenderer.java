/**
 * @(#)DebugListRenderer.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;
import net.sf.backcomp.debug.DebugMsg;

/**
 * 
 * Renders the List Cell of a Debug Message
 * 
 * @author Deathbob
 *
 */
class DebugListRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	/*
	 * Renders the Cell
	 */
	@Override
	public Component getListCellRendererComponent(
		final JList list,
		final Object value,
		final int index,
		final boolean isSelected,
		final boolean cellHasFocus )
	{
		final JPanel panel = new JPanel();
		final Border b =
			BorderFactory.createLineBorder( new Color(
				isSelected ? 255 : 0,
				0,
				0 ) );
		panel.setBorder( b );
		try
		{
			panel.setLayout( new BorderLayout() );
			
			JLabel a = null;
			if ( value instanceof DebugMsg )
			{
				final DebugMsg msg = ( DebugMsg ) value;
				final String color =
					( msg.getLevel().getForeColor() != null ) ? Integer
						.toHexString( msg.getLevel().getForeColor().getRGB() & 0x00ffffff )
						: "000000";//Text Color
				
				a =
					new JLabel( "<html><table style='color:#" + color
						+ "' width='200'><tr><td>" + msg.getMsg()
						+ "</td></tr><tr><td>" + msg.getLevel().toString()
						+ "</td></tr></table></html>" );
				panel.setBackground( isSelected ? ( ( msg.getLevel()
					.getLightColor() == null ) ? list.getSelectionBackground()
					: msg.getLevel().getLightColor() ) : msg.getLevel()
					.getDarkColor() );
			}
			else
			//Something is very wrong
			{
				a =
					new JLabel(
						"<html><table width='200'><tr><td>ERROR: Not a DebugMsg</td></tr></table></html>" );
				panel.setBackground( isSelected ? list.getSelectionBackground()
					: null );
			}
			panel.setForeground( isSelected ? list.getSelectionForeground()
				: null );
			panel.add( a, BorderLayout.CENTER );
			
		}
		catch ( final Exception ex )
		{
			Debug.message(
				"Unhandled Exception in DebugListRenderer",
				DebugLevel.Error,
				ex );
		}
		
		return panel;
	}
}
