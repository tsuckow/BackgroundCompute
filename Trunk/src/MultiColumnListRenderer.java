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
	/*private JPanel renderer;
	
	private JLabel second;

    public MultiColumnListRenderer()
    {        
        renderer = new JPanel();
		renderer.setLayout(new BoxLayout(renderer, BoxLayout.X_AXIS) );
 
		second = new JLabel();
		renderer.add(second );
    }*/

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
		JLabel a = new JLabel("<html><table style='border-style: solid; border-width: 1pt 1pt 1pt 1pt; width:146pt;'><tr><td>" + (String)value + "</td><td style='text-align:right;'>Running</td></tr></table></html>");
		
		
        wow.add(a,BorderLayout.CENTER);
       
        
        /*renderer.*/wow.setBackground(isSelected ? list.getSelectionBackground() : null);
		/*renderer.*/wow.setForeground(isSelected ? list.getSelectionForeground() : null);

    

        return wow;
    }    
}