/**
 * @(#)Settings.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import net.sf.backcomp.utils.*;

import java.awt.*;
import java.util.*;

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
		SettingsMsgHandler a = new SettingsMsgHandler();
		frame.addWindowListener(a);
		
		JPanel p = new JPanel(new SpringLayout());
		
		String[] settings = {"server_path", "locale"};
		
		int numitems = 0; //Number of rows in dialog
		for(String item : settings)
		{
			try
			{
				JLabel l = new JLabel((BC.LTextRB!=null)?BC.LTextRB.getString("Settings_" + item):"Error", JLabel.TRAILING);
    			p.add(l);
    			JTextField textField = new JTextField(SettMan.getSetting(item), 20);
    			l.setLabelFor(textField);
    			p.add(textField);
				
				++numitems;
			}
			catch(MissingResourceException e)
			{
				Tray.iconMessage("Opps", "Locale Data Missing", TrayIcon.MessageType.ERROR);
			}
		}
             	
				//Lay out the panel.
				SpringUtilities.makeCompactGrid(p,
                              numitems, 2, //rows, cols
                              6, 6,        //initX, initY
                              6, 6);       //xPad, yPad
                      
				frame.setContentPane(p);
				frame.pack();
				frame.setVisible(true);
	}
}