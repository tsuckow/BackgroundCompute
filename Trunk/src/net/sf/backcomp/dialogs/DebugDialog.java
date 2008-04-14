/**
 * @(#)DebugDialog.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import net.sf.backcomp.debug.*;

import java.util.Arrays;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;;


public final class DebugDialog
{
	private DebugDialog(){}
	
	private static JFrame frame = null;
	private static JList list = null;
	private static DebugMsg[] Msgs = null;
	private static javax.swing.Timer timer = null;
	
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
		frame = new JFrame("Debug Window");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		populate();
		
		//Go Live
		if(frame != null) frame.setVisible(true);
	}
	
	/**
	 * Populates the frame.
	 */
	private static void populate()
	{
		JPanel p = new JPanel(new BorderLayout());
		
		//Get List of Items
		Msgs = Debug.getArray();

		//Generate the list box
		list = new JList((Object[])Msgs); //data has type Object[]
		DebugListRenderer listRenderer = new DebugListRenderer();
		list.setCellRenderer(listRenderer);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(10);
		list.addListSelectionListener(new ListListen());
		//list.setSelectedIndex(0);	
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setMinimumSize(new Dimension(300,160));
		//listScroller.setPreferredSize(new Dimension(150, 250));
        
        p.add(listScroller,"Center");
        
        //Set timer for refreshing the list
        if(timer != null) timer.stop();
        timer = new javax.swing.Timer(100,new RefreshList(list));
		timer.start();
		
		frame.setContentPane(p);
		frame.pack();
		
		//Center frame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = frame.getSize();
		
		//listScroller.setSize(new Dimension(150, size.height));
		
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		frame.setLocation(x, y);
		
		listScroller.setPreferredSize(new Dimension(300, size.height));		
	}
	
	private static class RefreshList implements ActionListener
	{
		int regenlist = 1;
		JList list = null;
		public RefreshList(JList l)
		{
			list=l;
		}
		
		public void actionPerformed(ActionEvent e)
        {
			if( !list.isDisplayable() ) //Destroy Timer if window closed
			{
				timer.stop();
			}
			if( regenlist++ % 5 == 0)
			{
				DebugMsg[] temp = Debug.getArray();
				if( !Arrays.equals(temp,Msgs) )
				{
					Msgs = temp;
					int sel = list.getSelectedIndex();
					list.setListData(Msgs);
					list.setSelectedIndex(sel);
				}
				list.repaint();
			}		
        }
	}
	
	private static class ListListen implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
        {
        	if(list == null)
        	{
        		//Uhoh
        	}
        	else
        	{
        		if(list.getSelectedIndex() != -1)
        		{
        			//PluginInfo(info, installedPlugins[list.getSelectedIndex()]);
        		}
        	}
        }
	}
}
