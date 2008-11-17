package net.sf.backcomp.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.backcomp.debug.DebugMsg;

public class DebugEventDialog extends JDialog implements ActionListener
{
	/**
	 * Serial Number
	 */
	private static final long serialVersionUID = 1L;

	public DebugEventDialog(JFrame parent, DebugMsg event)
	{
		super(parent, "Debug", true);
		setModal(false);
		
		JPanel messagePane = new JPanel();
		messagePane.add(new JLabel(event.getMsg()));
		getContentPane().add(messagePane);
		
		JPanel buttonPane = new JPanel();
		JButton button = new JButton("OK"); 
		buttonPane.add(button); 
		
		button.addActionListener(this);
		
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setMinimumSize(new Dimension(200,100));
		
		pack(); //Compute Size

		//Handle Minimum Size
		/*
		if(size.width < 200) size.width = 200;
		if(size.height < 100) size.height = 100;
		setSize(size);*/
		
		if (parent != null)
		{
			Dimension parentSize = parent.getSize(); 
			Point p = parent.getLocation(); 
			setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		}
		else
		{
			final Dimension screenSize =
				Toolkit.getDefaultToolkit().getScreenSize();
			final Dimension size = getSize();
			screenSize.height = screenSize.height / 2;
			screenSize.width = screenSize.width / 2;
			size.height = size.height / 2;
			size.width = size.width / 2;
			final int y = screenSize.height - size.height;
			final int x = screenSize.width - size.width;
			setLocation( x, y );
		}
		
		setVisible(true);
	}
	  
	public void actionPerformed(ActionEvent e)
	{
		setVisible(false); 
		dispose(); 
	}
}
