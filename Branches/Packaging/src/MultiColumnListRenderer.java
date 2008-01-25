/**
 * @(#)MultiColumnListRenderer.java
 *
 *
 * @author 
 * @version 1.00 2007/6/21
 */

import javax.swing.*;
import java.awt.*;

public class MultiColumnListRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 1L;
	String [] status = new String[3];
	
    public MultiColumnListRenderer()
    {        
        status[0] = "images/status_green.png";
		status[1] = "images/status_red.png";
		status[2] = "images/status_blue.png";
    }
    
    private int stateCode(Plugin.state st)
    {
    	switch(st)
    	{
    		case Stopped:
    			return 1;
    		case Running:
    			return 0;
    		default:
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
 		
    		Plugin plug = Utils.loadPlugin( (String)value );
    		
    		
    		JLabel a = new JLabel("<html><table style='border-style: solid; border-width: 1pt 1pt 1pt 1pt; width:146pt;'><tr><td>" + plug.getName() + "</td><td style='text-align:right;'><img src='" + BC.class.getResource(status[ stateCode( plug.getState() ) ]) + "'></td></tr></table></html>");
		
    		wow.add(a,BorderLayout.CENTER);
       
    		/*renderer.*/wow.setBackground(isSelected ? list.getSelectionBackground() : null);
    		/*renderer.*/wow.setForeground(isSelected ? list.getSelectionForeground() : null);

    	}
    	catch(Exception ex)
    	{
    		Utils.iconMessage( ex.getMessage() ,"Unhandled Exception in Renderer", TrayIcon.MessageType.ERROR);				
    	}

        return wow;
    }    
    
}