/**
 * @(#)DebugDialog.java
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

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.backcomp.debug.Debug;
import net.sf.backcomp.debug.DebugMsg;

public final class DebugDialog
{
	private DebugDialog()
	{
	}
	
	private static JFrame frame = null;
	private static JList list = null;
	private static DebugMsg[] Msgs = null;
	private static javax.swing.Timer timer = null;
	
	/**
	 * Handles Initializing the frame.
	 */
	public final static void show()
	{
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
		frame = new JFrame( "Debug Window" );
		frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		
		populate();
		
		//Go Live
		if ( frame != null )
		{
			frame.setVisible( true );
		}
	}
	
	/**
	 * Populates the frame.
	 */
	private static void populate()
	{
		final JPanel p = new JPanel( new BorderLayout() );
		
		//Get List of Items
		Msgs = Debug.getArray();
		
		//Generate the list box
		list = new JList( Msgs ); //data has type Object[]
		final DebugListRenderer listRenderer = new DebugListRenderer();
		list.setCellRenderer( listRenderer );
		
		list.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
		//list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount( 10 );
		list.addListSelectionListener( new ListListen() );
		//list.setSelectedIndex(0);	
		final JScrollPane listScroller = new JScrollPane( list );
		listScroller.setMinimumSize( new Dimension( 300, 160 ) );
		//listScroller.setPreferredSize(new Dimension(150, 250));
		
		p.add( listScroller, "Center" );
		
		//Set timer for refreshing the list
		if ( timer != null )
		{
			timer.stop();
		}
		timer = new javax.swing.Timer( 100, new RefreshList( list ) );
		timer.start();
		
		frame.setContentPane( p );
		frame.pack();
		
		//Center frame
		final Dimension screenSize =
			Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension size = frame.getSize();
		
		//listScroller.setSize(new Dimension(150, size.height));
		
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		final int y = screenSize.height - size.height;
		final int x = screenSize.width - size.width;
		frame.setLocation( x, y );
		
		listScroller.setPreferredSize( new Dimension( 300, size.height ) );
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
			{
				timer.stop();
			}
			if ( regenlist++ % 5 == 0 )
			{
				final DebugMsg[] temp = Debug.getArray();
				if ( !Arrays.equals( temp, Msgs ) )
				{
					Msgs = temp;
					final int sel = list.getSelectedIndex();
					list.setListData( Msgs );
					list.setSelectedIndex( sel );
				}
				list.repaint();
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
			{
				//TODO:PluginInfo(info, installedPlugins[list.getSelectedIndex()]);
			}
		}
	}
}
