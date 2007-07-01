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
 
		/*second.*/setText( "<html><table style='width:100%; border-collapse: collapse;'><tr><td style='border-width: 1px 1px 1px 1px; padding: 1px 1px 1px 1px; border-style: dotted dotted dotted dotted; border-color: green green green green;'>" + (String)value + "</td><td style='border-width: 1px 1px 1px 1px; padding: 1px 1px 1px 1px; border-style: dotted dotted dotted dotted; border-color: green green green green;'>Testing...</td></tr></table></html>" );

		setBorder(BorderFactory)
        /*renderer.*/setBackground(isSelected ? list.getSelectionBackground() : null);
		/*renderer.*/setForeground(isSelected ? list.getSelectionForeground() : null);

    

        return this;
    }    
}