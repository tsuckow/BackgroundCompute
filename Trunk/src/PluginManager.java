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
	
	public static void show()
	{
		if(frame!=null)
		{
			if(frame.isShowing()) return;
			frame.dispose();
			frame = null;
		}
		
		frame = new JFrame("Plugin Manager");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel p = new JPanel();
		
		String[] it = {"Bla","Ble","Blue","Background Pi","SETI@home","Prime Numbers","Checksums"};
		
		JList list = new JList(it); //data has type Object[]
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
        Manage.add(new JButton("Remove"));
        
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
}