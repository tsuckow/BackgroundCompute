/**
 * @(#)PluginManager.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import net.sf.backcomp.plugins.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.Arrays;



public class PluginManager
{
	private static JFrame frame = null;
	private PluginManager(){}
	private static JList list = null;
	private static String[] installedPlugins = null;
	private static JLabel info = null;
	private static javax.swing.Timer timer = null;
	
	/**
	 * Creates and displays the PluginManager Dialog.
	 * If it is already showing it attempts to bring it to the foreground.
	 */
	public static void show()
	{
		//Does the dialog already exist?
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
		
		//Create Dialog
		frame = new JFrame("Project Manager");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setResizable(false);
		
		JPanel p = new JPanel();
		
		//Get the plugins to populate the list
		installedPlugins = PluginLoader.getLoadedPlugins();
		
		//Generate the list box
		list = new JList(/*pluginList*/installedPlugins); //data has type Object[]
		MultiColumnListRenderer renderer = new MultiColumnListRenderer();
		list.setCellRenderer(renderer);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(10);
		list.addListSelectionListener(new ListListen());
		//list.setSelectedIndex(0);	
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setMinimumSize(new Dimension(150,160));
		listScroller.setPreferredSize(new Dimension(150, 250));
        
        p.add(listScroller);
        
        //Set timer for refreshing the list
        if(timer != null) timer.stop();
        timer = new javax.swing.Timer(1000,new RefreshList(list));
		timer.start();
        
        JPanel RightSide = new JPanel();
        RightSide.setLayout(new BorderLayout());
        
        //Management Buttons
        JPanel Manage = new JPanel();
        JButton stop = new JButton("Stop");
        stop.addActionListener(new StopButton());
        Manage.add(stop);
        JButton pause = new JButton("Pause");
        pause.addActionListener(new PauseButton());
        Manage.add(pause);
        JButton remove = new JButton("Remove");
        remove.addActionListener(new RemoveButton());
        Manage.add(remove);
        JButton status = new JButton("Status");
        status.addActionListener(new StatusButton());
        Manage.add(status);
        
        //Plugin info section
        RightSide.add(Manage,BorderLayout.NORTH);
        info = new JLabel("Loading");
        info.setMinimumSize(new Dimension(320,160));
        info.setMaximumSize(new Dimension(320,160));
        PluginInfo(info,installedPlugins[0]);
        RightSide.add(info,BorderLayout.CENTER);
        
        p.add(RightSide);
                      
		frame.setContentPane(p);
		frame.pack();
		
		//Center frame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = frame.getSize();
		
		listScroller.setSize(new Dimension(150, size.height));
		
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		frame.setLocation(x, y);
		
		listScroller.setPreferredSize(new Dimension(150, size.height));
		
		//Go live
		frame.setVisible(true);
	}
	
	/**
	 * Generates the HTML for the info pane 
	 * 
	 * @param label The info JLabel
	 * @param name Plugin to get info for
	 */
	private static void PluginInfo(JLabel label, String name)
	{
			PluginHandler plug = PluginLoader.loadPlugin(name);
        	String Info = "<html>Error: Unable to load Plugin</html>";
        	if(plug!=null)
        	{
        		Info = plug.getInfo();
        	}
			label.setText("<html><div width='320px' height='160px'>" + Info + "</div><html>");
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
				String[] temp = PluginLoader.getLoadedPlugins();
				if( !Arrays.equals(temp,installedPlugins) )
				{
					installedPlugins = temp;
					list.setListData(installedPlugins);
				}
			}
			list.repaint();
        }
	}
	
	private static class StopButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
        {
        	if(list == null)
        	{
        		//Uhoh
        	}
        	else
        	{
        		if(list.getSelectedIndex() != -1)
        		{
        			//BC.PError("Got Selection: " + installedPlugins[list.getSelectedIndex()]);
        			PluginHandler plug = PluginLoader.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			plug.stop();
        			//frame.dispose();
					//frame = null;
					//show();
        		}
        	}
        }
	}
	
	private static class PauseButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
        {
        	if(list == null)
        	{
        		//Uhoh
        	}
        	else
        	{
        		if(list.getSelectedIndex() != -1)
        		{
        			//BC.PError("Got Selection: " + installedPlugins[list.getSelectedIndex()]);
        			PluginHandler plug = PluginLoader.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			plug.stop();
        			//frame.dispose();
					//frame = null;
					//show();
        		}
        	}
        }
	}
	
	private static class RemoveButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
        {
        	if(list == null)
        	{
        		//Uhoh
        	}
        	else
        	{
        		if(list.getSelectedIndex() != -1)
        		{
        			//BC.PError("Got Selection: " + installedPlugins[list.getSelectedIndex()]);
        			PluginHandler plug = PluginLoader.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			int n = JOptionPane.showConfirmDialog(
        				    frame,
        				    "Are you sure you want to uninstall: \"" + plug.getName() + "\"",
        				    "Remove Plugin?",
        				    JOptionPane.YES_NO_OPTION,
        				    JOptionPane.QUESTION_MESSAGE);
        			if(n == JOptionPane.YES_OPTION)
        			{
        				plug.uninstall();
        				frame.dispose();
        				frame = null;
        				show();
        			}
        		}
        	}
        }
	}
	
	private static class StatusButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
        {
        	if(list == null)
        	{
        		//Uhoh
        	}
        	else
        	{
        		if(list.getSelectedIndex() != -1)
        		{
        			//BC.PError("Got Selection: " + installedPlugins[list.getSelectedIndex()]);
        			PluginHandler plug = PluginLoader.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			JFrame bob = new JFrame(plug.getName());
        			bob.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        			
        			bob.setResizable(false);
        			
        			//FIXME: Check if null. If it is give some message saying Plugin does not support this.
        			bob.setContentPane(plug.getStatus());
        			bob.pack();
        			
        			//Center frame
        			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        			Dimension size = bob.getSize();
        			
        			screenSize.height = screenSize.height/2;
        			screenSize.width = screenSize.width/2;
        			size.height = size.height/2;
        			size.width = size.width/2;
        			int y = screenSize.height - size.height;
        			int x = screenSize.width - size.width;
        			bob.setLocation(x, y);
        			
        			bob.setVisible(true);
        			
        			//plug.stop();
        			//frame.dispose();
					//frame = null;
					//show();
        		}
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
        			PluginInfo(info, installedPlugins[list.getSelectedIndex()]);
        		}
        	}
        }
	}
}