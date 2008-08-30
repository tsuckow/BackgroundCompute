/**
 * @(#)Settings.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import net.sf.backcomp.utils.*;

import javax.swing.*;

public class Settings
{
	private static JFrame frame = null;
	private Settings(){}
	
	public static void show()
	{
		if(frame!=null)
		{
			if(frame.isShowing())
			{
				frame.toFront();
				return;
			}
			frame.dispose();
			frame = null;
		}
		
		frame = new JFrame("Settings");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		SettingsMsgHandler a = new SettingsMsgHandler();
		frame.addWindowListener(a);
		
		JPanel p = new JPanel(new SpringLayout());
		
		String[] settings = {"server_path", "locale","cpu_limit"};
		
		int numitems = 0; //Number of rows in dialog
		for(String item : settings)
		{
				JLabel l = new JLabel(LangMan.getString("Settings_" + item, item + ":"), JLabel.TRAILING);
    			p.add(l);
    			JTextField textField = new JTextField(SettingManager.getSetting(item) + (SettingManager.isDefaultSetting(item)?"~~":""), 20);
    			l.setLabelFor(textField);
    			p.add(textField);
    			JCheckBox tb = new JCheckBox("default");
    			p.add(tb);
				
				++numitems;
		}
             	
				//Lay out the panel.
				SpringUtilities.makeCompactGrid(p,
                              numitems, 3, //rows, cols
                              6, 6,        //initX, initY
                              6, 6);       //xPad, yPad
                      
				frame.setContentPane(p);
				frame.pack();
				frame.setVisible(true);
	}
}