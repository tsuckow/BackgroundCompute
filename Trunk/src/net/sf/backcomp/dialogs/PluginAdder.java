package net.sf.backcomp.dialogs;

/**
 * @(#)Settings.java
 *
 *
 * @author 
 * @version 1.00 2007/2/22
 */

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import net.sf.backcomp.plugins.PluginLoader;

public class PluginAdder
{
	private static JFrame frame = null;
	
	private PluginAdder()
	{
	}
	
	private static JList list = null;
	
	public static void show()
	{
		if ( frame != null )
		{
			if ( frame.isShowing() )
			{
				return;
			}
			frame.dispose();
			frame = null;
		}
		
		frame = new JFrame( "Project Manager" );
		frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		
		final JPanel p = new JPanel();
		
		final String[] installedPlugins = PluginLoader.getLoadedPlugins();
		final String[] pluginList = new String[installedPlugins.length];
		for ( int i = 0; i < installedPlugins.length; ++i )
		{
			pluginList[i] = installedPlugins[i].split( ";" )[0];
		}
		
		//String[] it = {"Bla","Ble","Blue","Background Pi","SETI@home","Prime Numbers","Checksums"};
		
		list = new JList( pluginList ); //data has type Object[]
		list.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
		list.setLayoutOrientation( JList.HORIZONTAL_WRAP );
		list.setVisibleRowCount( -1 );
		
		final JScrollPane listScroller = new JScrollPane( list );
		listScroller.setPreferredSize( new Dimension( 100, 80 ) );
		
		p.add( listScroller );
		
		final JPanel RightSide = new JPanel();
		RightSide.setLayout( new BorderLayout() );
		
		final JPanel Manage = new JPanel();
		Manage.add( new JButton( "Install" ) );
		
		RightSide.add( Manage, BorderLayout.NORTH );
		RightSide.add( new PluginInfo(), BorderLayout.CENTER );
		
		p.add( RightSide );
		
		frame.setContentPane( p );
		frame.pack();
		frame.setVisible( true );
	}
	
	private static class PluginInfo extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		PluginInfo()
		{
			add( new JLabel(
				"<html>Hi<img src='http://defcon1.hopto.org/Title2.gif'></html>" ) );
		}
	}
}