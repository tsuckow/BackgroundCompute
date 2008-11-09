package net.sf.backcomp.dialogs;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutDialog
{
	private AboutDialog(){}
	
	private static JFrame frame = null;
	
	/**
	 * Handles Initializing the frame.
	 */
	public final static void show()
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
		frame = new JFrame("About Background Compute");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//Add the background Image
		final JLabel logo =
			new JLabel(
				new ImageIcon(
					"images" + File.separator + "Logo.png",
					"Updater"
				)
			);
		frame.add( logo );
		
		//Go Live
		if(frame != null) frame.setVisible(true);
	}
}
