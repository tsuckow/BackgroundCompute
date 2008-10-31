/**
 * @(#)MultiColumnListRenderer.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugLevel;
import net.sf.backcomp.plugins.*;

import javax.swing.*;
import java.awt.*;

class MultiColumnListRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 1L;
	String [] status = new String[4];
	
    public MultiColumnListRenderer()
    {        
        status[0] = "images/status_green.png";
		status[1] = "images/status_red.png";
		status[2] = "images/status_blue.png";
		status[3] = "images/status_orange.png";
    }
    
    private int stateCode(PluginHandler pm)
    {
    	if( pm.isActive() )
    	{
    		if( pm.isPaused() )
    			return 3;
    		else
    			return 0;
    	}
    	else if( pm.isStopped() )
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
    public Component getListCellRendererComponent(
                                       JList list,
                                       Object value,
                                       int index,
                                       boolean isSelected,
                                       boolean cellHasFocus)
    {
    	
    	
    	JPanel wow = new JPanel();
    	try
    	{
    		wow.setLayout(new BorderLayout());
 		
    		PluginHandler plug = PluginLoader.loadPlugin( (String)value );
    		
    		int state = 2;
    		String name = "Plugin No Longer Exists";
    		
    		if(plug != null)	
    		{
    			state = stateCode( plug );
    			name = plug.getName();
    		}
    		
    		
    		String image = ClassLoader.getSystemResource(status[ state ]).toString();
    		
    		JLabel a = new JLabel("<html><table style='border-style: solid; border-width: 1pt 1pt 1pt 1pt; width:146pt;'><tr><td>" + name + "</td><td style='text-align:right;'><img src='" + image + "'></td></tr></table></html>");
		
    		wow.add(a,BorderLayout.CENTER);
       
    		/*renderer.*/wow.setBackground(isSelected ? list.getSelectionBackground() : null);
    		/*renderer.*/wow.setForeground(isSelected ? list.getSelectionForeground() : null);

    	}
    	catch(Exception ex)
    	{
    		Debug.messageDlg("Unhandled Exception in Plugin Manager List Renderer", DebugLevel.Error, ex);				
    	}

        return wow;
    }    
    
}