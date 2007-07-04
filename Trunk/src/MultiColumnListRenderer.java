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
	String [] status = new String[3];
    public MultiColumnListRenderer()
    {        
        status[0] = "images/status_green.png";
		status[1] = "images/status_red.png";
		status[2] = "images/status_blue.png";
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
                                       boolean cellHasFocus) {

		//Item item = (Item)value;
 		JPanel wow = new JPanel();
 		wow.setLayout(new BorderLayout());
		/*second.*///setText( + "</td><td style='border-width: 1px 1px 1px 1px; padding: 1px 1px 1px 1px; border-style: dotted dotted dotted dotted; border-color: green green green green;'>Testing...</td></tr></table></html>" );
		//setText(" ");
		JLabel a = new JLabel("<html><table style='border-style: solid; border-width: 1pt 1pt 1pt 1pt; width:146pt;'><tr><td>" + (String)value + "</td><td style='text-align:right;'><img src='" + BC.class.getResource(status[0]) + "'></td></tr></table></html>");
		
		
        wow.add(a,BorderLayout.CENTER);
       
        
        /*renderer.*/wow.setBackground(isSelected ? list.getSelectionBackground() : null);
		/*renderer.*/wow.setForeground(isSelected ? list.getSelectionForeground() : null);

    

        return wow;
    }    
}