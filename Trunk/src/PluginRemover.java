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

import java.io.*;//File

public class PluginRemover
{
	private static JFrame frame = null;
	private PluginRemover(){}
	private static JList list = null;
	private static String[] installedPlugins = null;
	private static JPanel info = null;
	
	public static void show()
	{
		if(frame!=null)
		{
			if(frame.isShowing()) return;
			frame.dispose();
			frame = null;
		}
		
		frame = new JFrame("Project Remover");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel p = new JPanel();
		
		installedPlugins = getPlugins();
		String[] pluginList = new String[installedPlugins.length];
		for(int i = 0; i < installedPlugins.length; ++i)
		{
			Plugin plug = Utils.loadPlugin(installedPlugins[i]);
			pluginList[i] = plug.getName();
		}
				
		list = new JList(pluginList); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		list.addListSelectionListener(new ListListen());
				
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(100, 80));
        
        p.add(listScroller);
        
        JPanel RightSide = new JPanel();
        RightSide.setLayout(new BorderLayout());
        
        JPanel Manage = new JPanel();
        JButton remove = new JButton("Remove");
        remove.addActionListener(new RemoveButton());
        Manage.add(remove);
        
        RightSide.add(Manage,BorderLayout.NORTH);
        
        info = new PluginInfo(installedPlugins[0]);
        RightSide.add(info,BorderLayout.CENTER);
        
        p.add(RightSide);
                      
		frame.setContentPane(p);
		frame.pack();
		frame.setVisible(true);
	}
	
	private static class PluginInfo extends JPanel
	{
		PluginInfo(String name)
		{
			Plugin plug = Utils.loadPlugin(name);
        	String Info = "<html>Error: Unable to load Plugin</html>";
        	if(plug!=null)
        	{
        		Info = plug.getInfo();
        	}
			add(new JLabel(Info));
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
        			BC.PError("Got Selection: " + installedPlugins[list.getSelectedIndex()]);
        			Plugin plug = Utils.loadPlugin(installedPlugins[list.getSelectedIndex()]);
        			plug.startRemove();
        			frame.dispose();
					frame = null;
					show();
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
        			info = new PluginInfo(installedPlugins[list.getSelectedIndex()]);
        		}
        	}
        }
	}
	
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
	}
}