/**
 * @(#)PluginManager.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 *
 */
package net.sf.backcomp.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.backcomp.plugins.PluginHandler;
import net.sf.backcomp.plugins.PluginLoader;

public class PluginManager
{
	private static JFrame frame = null;
	
	private PluginManager()
	{
	}
	
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
		if ( frame != null )
		{
			if ( frame.isShowing() )
			{
				frame.toFront();
				return;
			}
			frame.dispose();
			frame = null;
		}
		
		//Create Dialog
		frame = new JFrame( "Project Manager" );
		frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		
		frame.setResizable( false );
		
		final JPanel p = new JPanel();
		
		//Get the plugins to populate the list
		installedPlugins = PluginLoader.getLoadedPlugins();
		
		//Generate the list box
		list = new JList( installedPlugins ); //data has type Object[]
		final MultiColumnListRenderer renderer = new MultiColumnListRenderer();
		list.setCellRenderer( renderer );
		
		list.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount( 10 );
		list.addListSelectionListener( new ListListen() );
		//list.setSelectedIndex(0);	
		final JScrollPane listScroller = new JScrollPane( list );
		listScroller.setMinimumSize( new Dimension( 150, 160 ) );
		listScroller.setPreferredSize( new Dimension( 150, 250 ) );
		
		p.add( listScroller );
		
		//Set timer for refreshing the list
		if ( timer != null )
			timer.stop();
		timer = new javax.swing.Timer( 1000, new RefreshList( list ) );
		timer.start();
		
		final JPanel RightSide = new JPanel();
		RightSide.setLayout( new BorderLayout() );
		
		//Management Buttons
		final JPanel Manage = new JPanel();
		final JButton stop = new JButton( "Stop" );
		stop.addActionListener( new StopButton() );
		Manage.add( stop );
		final JButton pause = new JButton( "Pause" );
		pause.addActionListener( new PauseButton() );
		Manage.add( pause );
		final JButton remove = new JButton( "Remove" );
		remove.addActionListener( new RemoveButton() );
		Manage.add( remove );
		final JButton status = new JButton( "Status" );
		status.addActionListener( new StatusButton() );
		Manage.add( status );
		
		//Plugin info section
		RightSide.add( Manage, BorderLayout.NORTH );
		info = new JLabel( "Loading" );
		info.setMinimumSize( new Dimension( 320, 160 ) );
		info.setMaximumSize( new Dimension( 320, 160 ) );
		PluginInfo( info, installedPlugins[0] );
		RightSide.add( info, BorderLayout.CENTER );
		
		p.add( RightSide );
		
		frame.setContentPane( p );
		frame.pack();
		
		//Center frame
		final Dimension screenSize =
		    Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension size = frame.getSize();
		
		listScroller.setSize( new Dimension( 150, size.height ) );
		
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		final int y = screenSize.height - size.height;
		final int x = screenSize.width - size.width;
		frame.setLocation( x, y );
		
		listScroller.setPreferredSize( new Dimension( 150, size.height ) );
		
		//Go live
		frame.setVisible( true );
	}
	
	/**
	 * Generates the HTML for the info pane 
	 * 
	 * @param label The info JLabel
	 * @param name Plugin to get info for
	 */
	private static void PluginInfo( final JLabel label, final String name )
	{
		final PluginHandler plug = PluginLoader.loadPlugin( name );
		String Info = "<html>Error: Unable to load Plugin</html>";
		if ( plug != null )
		{
			Info = plug.getInfo();
			if ( Info == null )
				Info = "Information Not Available";
		}
		else
			Info = "Selected Plugin Does Not Exist.";
		label.setText( "<html><div width='320px' height='160px'>" + Info
		    + "</div><html>" );
	}
	
	private static class RefreshList implements ActionListener
	{
		int regenlist = 1;
		JList list = null;
		
		public RefreshList( final JList l )
		{
			list = l;
		}
		
		public void actionPerformed( final ActionEvent e )
		{
			if ( !list.isDisplayable() )
				timer.stop();
			if ( regenlist++ % 5 == 0 )
			{
				final String[] temp = PluginLoader.getLoadedPlugins();
				if ( !Arrays.equals( temp, installedPlugins ) )
				{
					installedPlugins = temp;
					list.setListData( installedPlugins );
				}
			}
			list.repaint();
		}
	}
	
	private static class StopButton implements ActionListener
	{
		public void actionPerformed( final ActionEvent e )
		{
			if ( list == null )
			{
				//TODO:Uhoh
			}
			else if ( list.getSelectedIndex() != -1 )
			{
				final PluginHandler plug =
				    PluginLoader.loadPlugin( installedPlugins[list
				        .getSelectedIndex()] );
				plug.stop();
				//frame.dispose();
				//frame = null;
				//show();
			}
		}
	}
	
	private static class PauseButton implements ActionListener
	{
		public void actionPerformed( final ActionEvent e )
		{
			if ( list == null )
			{
				//TODO:Uhoh
			}
			else if ( list.getSelectedIndex() != -1 )
			{
				final PluginHandler plug =
				    PluginLoader.loadPlugin( installedPlugins[list
				        .getSelectedIndex()] );
				plug.stop();
				//frame.dispose();
				//frame = null;
				//show();
			}
		}
	}
	
	private static class RemoveButton implements ActionListener
	{
		public void actionPerformed( final ActionEvent e )
		{
			if ( list == null )
			{
				//TODO: Uhoh
			}
			else if ( list.getSelectedIndex() != -1 )
			{
				final PluginHandler plug =
				    PluginLoader.loadPlugin( installedPlugins[list
				        .getSelectedIndex()] );
				final int n =
				    JOptionPane.showConfirmDialog(
				        frame,
				        "Are you sure you want to uninstall: \""
				            + plug.getName() + "\"",
				        "Remove Plugin?",
				        JOptionPane.YES_NO_OPTION,
				        JOptionPane.QUESTION_MESSAGE );
				if ( n == JOptionPane.YES_OPTION )
				{
					plug.uninstall();
					frame.dispose();
					frame = null;
					show();
				}
			}
		}
	}
	
	private static class StatusButton implements ActionListener
	{
		public void actionPerformed( final ActionEvent e )
		{
			if ( list == null )
			{
				//TODO:Uhoh
			}
			else if ( list.getSelectedIndex() != -1 )
			{
				final PluginHandler plug =
				    PluginLoader.loadPlugin( installedPlugins[list
				        .getSelectedIndex()] );
				final JFrame bob = new JFrame( plug.getName() );
				bob.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
				
				bob.setResizable( false );
				
				//FIXME: Check if null. If it is give some message saying
				//Plugin does not support this.
				bob.setContentPane( plug.getStatus() );
				bob.pack();
				
				//Center frame
				final Dimension screenSize =
				    Toolkit.getDefaultToolkit().getScreenSize();
				final Dimension size = bob.getSize();
				
				screenSize.height = screenSize.height / 2;
				screenSize.width = screenSize.width / 2;
				size.height = size.height / 2;
				size.width = size.width / 2;
				final int y = screenSize.height - size.height;
				final int x = screenSize.width - size.width;
				bob.setLocation( x, y );
				
				bob.setVisible( true );
				
				//plug.stop();
				//frame.dispose();
				//frame = null;
				//show();
			}
		}
	}
	
	private static class ListListen implements ListSelectionListener
	{
		public void valueChanged( final ListSelectionEvent e )
		{
			if ( list == null )
			{
				//TODO:Uhoh
			}
			else if ( list.getSelectedIndex() != -1 )
				PluginInfo( info, installedPlugins[list.getSelectedIndex()] );
		}
	}
}