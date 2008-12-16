/**
 * @(#)DebugListRenderer.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import net.sf.backcomp.debug.*;

import javax.swing.*;

import java.awt.*;

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
    public Component getListCellRendererComponent(
                                       JList list,
                                       Object value,
                                       int index,
                                       boolean isSelected,
                                       boolean cellHasFocus)
    { 	
    	JPanel panel = new JPanel();
    	
    	try
    	{
    		panel.setLayout(new BorderLayout());
 		
    		JLabel a = null;
    		if(value instanceof DebugMsg)
    		{
    			DebugMsg msg = (DebugMsg)value;
    			String color = (msg.getLevel().getForeColor()!=null)?Integer.toHexString( msg.getLevel().getForeColor().getRGB() & 0x00ffffff ):"000000";//Text Color
    			
    			a = new JLabel("<html><table style='border-style: solid; " + (isSelected?"border-color:#FF0000;":"") + " border-width: 1pt 1pt 1pt 1pt; color:#" + color + "' width='200'><tr><td>" + msg.getMsg() + "</td></tr><tr><td>" + msg.getLevel().toString() + "</td></tr></table></html>");
    			panel.setBackground(isSelected ? ((msg.getLevel().getLightColor()==null)?list.getSelectionBackground():msg.getLevel().getLightColor()) : msg.getLevel().getDarkColor());
    		}
    		else//Something is very wrong
    		{
    			a = new JLabel("<html><table style='border-style: solid; border-width: 1pt 1pt 1pt 1pt;' width='200'><tr><td>ERROR: Not a DebugMsg</td></tr></table></html>");
    			panel.setBackground(isSelected ? list.getSelectionBackground() : null);
    		}
    		panel.setForeground(isSelected ? list.getSelectionForeground() : null);
    		panel.add(a,BorderLayout.CENTER);

    	}
    	catch(Exception ex)
    	{
    		Debug.message("Unhandled Exception in DebugListRenderer", DebugLevel.Error, ex);			
    	}

        return panel;
    }
}
