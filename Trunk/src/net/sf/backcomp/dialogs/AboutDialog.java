package net.sf.backcomp.dialogs;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class AboutDialog
{
	private AboutDialog()
	{
	}
	
	private static JFrame frame = null;
	
	/**
	 * Handles Initializing the frame.
	 */
	public static final void show()
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
		frame = new JFrame( "About Background Compute" );
		frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		final JPanel p = new JPanel();
		//Add the background Image
		final JLabel logo =
			new JLabel( new ImageIcon(
				"images" + File.separator + "Logo.png",
				"Updater" ) );
		p.add( logo );
		frame.add( p );
		
		frame.pack();
		
		//Go Live
		if ( frame != null )
		{
			frame.setVisible( true );
		}
	}
}
