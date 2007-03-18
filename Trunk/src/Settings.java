/**
 * @(#)Settings.java
 *
 *
 * @author 
 * @version 1.00 2007/2/22
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Settings
{
	private static JFrame frame = null;
	private Settings(){}
	
	public static void show()
	{
		if(frame!=null)
		{
			if(frame.isShowing()) return;
			frame.dispose();
			frame = null;
		}
		
		frame = new JFrame("Settings");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel p = new JPanel(new SpringLayout());
				
				JLabel l = new JLabel("Server Path: ", JLabel.TRAILING);
    			p.add(l);
    			JTextField textField = new JTextField(BC.Settings.getProperty("server_path"), 10);
    			l.setLabelFor(textField);
    			p.add(textField);
    			
    			l = new JLabel("Language: ", JLabel.TRAILING);
    			p.add(l);
    			textField = new JTextField(BC.Settings.getProperty("locale"), 10);
    			l.setLabelFor(textField);
    			p.add(textField);
    			
    			l = new JLabel("Test: ", JLabel.TRAILING);
    			p.add(l);
    			textField = new JTextField(10);
    			l.setLabelFor(textField);
    			p.add(textField);
             	
				//Lay out the panel.
				SpringUtilities.makeCompactGrid(p,
                              3, 2, //rows, cols
                              6, 6,        //initX, initY
                              6, 6);       //xPad, yPad
                      
				frame.setContentPane(p);
				frame.pack();
				frame.setVisible(true);
	}
}