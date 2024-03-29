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



public class PluginManager
{
	private static JFrame frame = null;
	private PluginManager(){}
	private static JList list = null;
	private static String[] installedPlugins = null;
	private static JLabel info = null;
	private static javax.swing.Timer timer = null;
	
	public static void show()
	{
		if(frame!=null)
		{
			if(frame.isShowing()) return;
			frame.dispose();
			frame = null;
		}
		
		frame = new JFrame("Project Manager");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setResizable(false);
		
		JPanel p = new JPanel();
		
		installedPlugins = PluginLoader.getLocalPlugins();
		
		//TODO: THIS MAY BE NO LONGER NEEDED
		String[] pluginList = new String[installedPlugins.length];
		for(int i = 0; i < installedPlugins.length; ++i)
		{
			
			pluginList[i] = installedPlugins[i];
		}
				
		list = new JList(pluginList); //data has type Object[]
		MultiColumnListRenderer renderer = new MultiColumnListRenderer();
		list.setCellRenderer(renderer);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(10);
		list.addListSelectionListener(new ListListen());
				
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setMinimumSize(new Dimension(150,160));
		listScroller.setPreferredSize(new Dimension(150, 250));
        
        p.add(listScroller);
        
        if(timer != null) timer.stop();
        timer = new javax.swing.Timer(1000,new RefreshList(list));
		timer.start();
        
        JPanel RightSide = new JPanel();
        RightSide.setLayout(new BorderLayout());
        
        JPanel Manage = new JPanel();
        JButton stop = new JButton("Stop");
        stop.addActionListener(new StopButton());
        Manage.add(stop);
        JButton remove = new JButton("Remove");
        remove.addActionListener(new RemoveButton());
        Manage.add(remove);
        JButton status = new JButton("Status");
        status.addActionListener(new StatusButton());
        Manage.add(status);
        
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
		
		frame.setVisible(true);
	}
	
	private static void PluginInfo(JLabel label, String name)
	{
			Plugin plug = PluginLoader.loadPlugin(name);
        	String Info = "<html>Error: Unable to load Plugin</html>";
        	if(plug!=null)
        	{
        		Info = plug.getInfo();
        	}
			label.setText("<html><div width='320px' height='160px'>" + Info + "</div><html>");
	}
	
	private static class RefreshList implements ActionListener
	{
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
        			Plugin plug = PluginLoader.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			plug.stopAll(true);
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
        			Plugin plug = PluginLoader.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			int n = JOptionPane.showConfirmDialog(
        				    frame,
        				    "Are you sure you want to uninstall: \"" + plug.getName() + "\"",
        				    "Remove Plugin?",
        				    JOptionPane.YES_NO_OPTION,
        				    JOptionPane.QUESTION_MESSAGE);
        			if(n == JOptionPane.YES_OPTION)
        			{
        				plug.startRemove();
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
        			Plugin plug = PluginLoader.loadPlugin(installedPlugins[list.getSelectedIndex()]);
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
	/*
	private static String[] getPlugins()
	{
		String Dir = "plugins/";
		
		File[] files = null;
		FilenameFilter filter = new FilenameFilter(){
        	public boolean accept(File dir, String name)
        	{
        		File pdir = new File(dir,name);
            	if( pdir.isDirectory() )
            	{
            		if(Utils.loadPlugin(name)!=null)
            		{
            			return true;
            		}
            	}
            	
            	return false;
        	}
    	};
    	
    	File src = new File(Dir);
    	files = src.listFiles(filter);
    	
    	String[] plugins = new String[files.length];
    	for(int i = 0; i < files.length; ++i)
    	{
    		plugins[i] = files[i].getName();
    	}
    	
    	return plugins;
	}*/
}