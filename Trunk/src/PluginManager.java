/**
 * @(#)Settings.java
 *
 *
 * @author 
 * @version 1.00 2007/2/22
 */

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
		
		JPanel p = new JPanel();
		
		installedPlugins = Utils.getLocalPlugins();
		
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
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(new ListListen());
				
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(150, 80));
        
        p.add(listScroller);
        
        if(timer != null) timer.stop();
        timer = new javax.swing.Timer(2000,new RefreshList(list));
		timer.start();
        
        JPanel RightSide = new JPanel();
        RightSide.setLayout(new BorderLayout());
        
        JPanel Manage = new JPanel();
        JButton stop = new JButton("Stop");
        stop.addActionListener(new StopButton());
        Manage.add(stop);
        
        RightSide.add(Manage,BorderLayout.NORTH);
        info = new JLabel("Loading");
        PluginInfo(info,installedPlugins[0]);
        RightSide.add(info,BorderLayout.CENTER);
        
        p.add(RightSide);
                      
		frame.setContentPane(p);
		frame.pack();
		frame.setVisible(true);
	}
	
	private static void PluginInfo(JLabel label, String name)
	{
			Plugin plug = Utils.loadPlugin(name);
        	String Info = "<html>Error: Unable to load Plugin</html>";
        	if(plug!=null)
        	{
        		Info = plug.getInfo();
        	}
			label.setText(Info);
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
        			BC.PError("Got Selection: " + installedPlugins[list.getSelectedIndex()]);
        			Plugin plug = Utils.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			plug.stop();
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