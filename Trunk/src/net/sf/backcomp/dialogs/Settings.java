/**
 * @(#)Settings.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.sf.backcomp.utils.LangMan;
import net.sf.backcomp.utils.SettingManager;
import net.sf.backcomp.utils.SpringUtilities;

public class Settings
{
	private static JFrame frame = null;
	
	private Settings()
	{
	}
	
	public static void show()
	{
		if ( frame != null )
		{
			if ( frame.isShowing() )
			{
				frame.toFront();
				return;
			}
			frame.dispose();
			frame = null;
		}
		
		frame = new JFrame( "Settings" );
		frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		final SettingsMsgHandler a = new SettingsMsgHandler();
		frame.addWindowListener( a );
		
		final JPanel p = new JPanel( new SpringLayout() );
		
		final String[] settings = { "server_path", "locale", "cpu_limit" };
		
		int numitems = 0; //Number of rows in dialog
		for ( final String item : settings )
		{
			final JLabel l =
				new JLabel(
					LangMan.getString( "Settings_" + item, item + ":" ),
					SwingConstants.TRAILING );
			p.add( l );
			final JTextField textField =
				new JTextField(
					SettingManager.getSetting( item )
						+ ( SettingManager.isDefaultSetting( item ) ? "~~" : "" ),
					20 );
			l.setLabelFor( textField );
			p.add( textField );
			final JCheckBox tb = new JCheckBox( "default" );
			p.add( tb );
			
			++numitems;
		}
		
		//Lay out the panel.
		SpringUtilities.makeCompactGrid( p, numitems, 3, //rows, cols
			6,
			6, //initX, initY
			6,
			6 ); //xPad, yPad
		
		frame.setContentPane( p );
		frame.pack();
		frame.setVisible( true );
	}
}