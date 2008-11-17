package net.sf.backcomp.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
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
		
		if (parent != null)
		{
			Dimension parentSize = parent.getSize(); 
			Point p = parent.getLocation(); 
			setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
		}
		
		JPanel messagePane = new JPanel();
		messagePane.add(new JLabel(event.getMsg()));
		getContentPane().add(messagePane);
		
		JPanel buttonPane = new JPanel();
		JButton button = new JButton("OK"); 
		buttonPane.add(button); 
		
		button.addActionListener(this);
		
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		pack(); 
		setVisible(true);
	}
	  
	public void actionPerformed(ActionEvent e)
	{
		setVisible(false); 
		dispose(); 
	}
}
