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

public class PluginRemover
{
	private static JFrame frame = null;
	private PluginRemover(){}
	private static JList list = null;
	
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
		
		String[] installedPlugins = BC.getLocalList("installedplugins.txt");
		String[] pluginList = new String[installedPlugins.length];
		for(int i = 0; i < installedPlugins.length; ++i)
		{
			pluginList[i] = installedPlugins[i].split(";")[0];
		}
				
		list = new JList(pluginList); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
				
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
        
        RightSide.add(new PluginInfo("BP"),BorderLayout.CENTER);
        
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
        			 BC.PError("Got Selection");
        		}
        	}
        }
	}
}