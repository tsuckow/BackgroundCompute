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

public class PluginManager
{
	private static JFrame frame = null;
	private PluginManager(){}
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
		
		String[] installed = BC.getLocalList("installedPlugins.txt");
		String[] master = BC.getRemoteList("plugins.txt");
		
		String[] it = new String[installed.length];
		for(int i = 0; i < installed.length; ++i)
		{
			it[i] = installed[i].split(";")[1];
		}
		
		//String[] it = {"Bla","Ble","Blue","Background Pi","SETI@home","Prime Numbers","Checksums"};
		
		list = new JList(it); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
				
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(100, 80));
        
        p.add(listScroller);
        
        JPanel RightSide = new JPanel();
        RightSide.setLayout(new BorderLayout());
        
        JPanel Manage = new JPanel();
        Manage.add(new JButton("Install"));
        JButton remove = new JButton("Remove");
        remove.addActionListener(new RemoveButton());
        Manage.add(remove);
        
        RightSide.add(Manage,BorderLayout.NORTH);
        RightSide.add(new PluginInfo(),BorderLayout.CENTER);
        
        p.add(RightSide);
                      
		frame.setContentPane(p);
		frame.pack();
		frame.setVisible(true);
	}
	private static class PluginInfo extends JPanel
	{
		PluginInfo()
		{
			add(new JLabel("<html>Hi<img src='http://defcon1.hopto.org/Title2.gif'></html>"));
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