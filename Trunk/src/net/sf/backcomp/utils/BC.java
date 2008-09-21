/**
 * @(#)BPC.java
 * 
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2008 Thomas Suckow (Deathbob)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * support@defcon1.hopto.org
 * 
 * 
 */
package net.sf.backcomp.utils;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

/**
 * The Loader for Background Compute.
 * 
 * @author Deathbob
 * @version 0.2.1 2008/08/17
 *
 */
public final class BC extends SwingWorker<Object, Object[]>
{

	/**
	 * Only we can create ourself.
	 */
	private BC() {}
	
	//Private Init's
	
	/**
	 * Prepares default settings into a properties object
	 * 
	 * @return Default settings
	 */
	private static Properties defaultSettings()
	{
		final Properties set = new Properties();
		
		set.setProperty( "cpu_limit", "80" );
		set.setProperty( "locale", "en" );
		set.setProperty(
			"server_path",
			"http://defcon1.hopto.org/updates/bc/"
		);
		set.setProperty( "updateError", "False" );
		
		return set;
	}
	
	/**
	 * Classpath of runtime.
	 */
	static final String    CLASS_PATH = getClassPath().trim();
	
	/**
	 * The systems line separator.
	 */
	static final String    NEW_LINE = System.getProperty( "line.separator" );
	
	/**
	 * Width of splash window.
	 */
	private static final int      SPLASH_HEIGHT = 100;
	
	/**
	 * Height of splash window.
	 */
	private static final int      SPLASH_WIDTH = 600;
	
	/**
	 * 100%
	 */
	private static final int      HUNDRED_PERCENT = 100;
	
	/**
	 * One second in milliseconds.
	 */
	private static final int ONE_SECOND = 1000;
	
	/**
	 * One computer kilo
	 */
	private static final int ONE_KILO = 1024;
	
	/**
	 * Seconds to wait before restarting after updating.
	 */
	private static final int UPDATE_RESTART_DELAY = 3;
	
	/**
	 * Seconds to wait before restarting after an error.
	 */
	private static final int ERROR_RESTART_DELAY = 5;
	
	/**
	 * Text box for splash screen.
	 */
	private JLabel mSplashText;
	
	/**
	 * Frame for slash screen.
	 */
	private JWindow mSplashFrame;
	
	/**
	 * Current item progress bar ID.
	 */
	private static final Integer  NUM_ITEMPB = 0;
	
	/**
	 * Overall progress bar ID.
	 */
	private static final Integer  NUM_OVERALLPB = 1;
	
	/**
	 * Progress bar object array.
	 */
	private JProgressBar[] mSplashProgressBars = new JProgressBar[2];
	
	//Package Variables
	
	/**
	 * The Default Settings
	 */
	static final Properties DEFAULT_SETTINGS = defaultSettings();
	
	/**
	 * Current Settings
	 */
	static final Properties SETTINGS = new Properties( DEFAULT_SETTINGS );
	
	/**
	 * Language Localization Bundle
	 */
	private static ResourceBundle sLanguageBundle;
	
	//Functions
	
	/**
	 * Entry Point.
	 * 
	 * @param args Command Line Arguments
	 */
	public static void main( String[] args )
	{
		try
		{
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName()
			);
		}
		catch ( Exception e )
		{
			e = null;
			//Hmm, Don't Care
		}
		
		//Start the self enclosed SwingWorker.
		( new BC() ).execute();
		
	}
	
	
	//
	//Swing Worker Meat & Potato's
	//
	
	
	
	
	/**
	 * Task to perform off of the event thread.
	 * 
	 * @return nothing
	 */
	@Override
	protected Object doInBackground()
	{
		try
		{
			doWork();
		}
		catch ( Exception ex )
		{
			//!Don't Localize
			showError(
				"Caught exception at top of Background Thread."
				+ "\n\nLast Chance.\n\n"
				+ ex.toString() + "\n\n" + makeStackTrace( ex )
			);
		}
		
		
		//I have to return something so return nothing.
		return new Object();
	}
	
	
	
	
	/**
	 * Handles the splash screen and all tasks performed involving it,
	 * including updating the application.
	 */
	private void doWork()
	{
		//Display the Splash Screen
		publish( new Object[] {} );
		
		//
		//Load Settings
		
		loadSettings();
		
		
		//
		//Load Localization
		
		loadLocalization();
		
		//
		//Do Update
		
		performUpdate();
		
		//
		//Verify Localization Exists, Restart if problem.
		if ( sLanguageBundle == null )
		{
			updateError( "No Localization is loaded. Cannot continue.", null );
		}
		
		
		//
		//Commit Settings
		SETTINGS.setProperty( "updateError", "False" );
		FileOutputStream setOS = null;
		try
		{
			setOS = new FileOutputStream( "Settings.properties" );
			SETTINGS.store( setOS , "Background Compute" );
		}
		catch ( FileNotFoundException ex )
		{
			final String defErrMsg = "Problem saving settings.\n\nError: ";
			final String details = "\n\n" + makeStackTrace( ex );
			showError( localize( "Error_Settings1", defErrMsg ) + details );
		}
		catch ( IOException ex )
		{
			final String defErrMsg = "Problem saving settings.\n\nError: ";
			final String details = "\n\n" + makeStackTrace( ex );
			showError( localize( "Error_Settings1", defErrMsg ) + details );
		}
		finally
		{
			if ( setOS != null )
			{
				try
				{
					setOS.close();
				}
				catch ( IOException ex )
				{
					final String defErrMsg =
						"Problem saving settings.\n\nError: ";
					final String details = "\n\n" + makeStackTrace( ex );
					showError(
						localize( "Error_Settings1", defErrMsg )
						+ details
					);
				}
				setOS = null;
			}
		}
		
		//Start the main application
		javax.swing.SwingUtilities.invokeLater( new Mainapp() );
		
		//SwingWorker dies. Splash Screen Dies
	}

	/**
	 * Does the meat and potatoes of updating
	 */
	private void performUpdate()
	{
		boolean updated = false;
		
		//Downloading Lists...
		setSplashText( localize( "Lists1", "Downloading Lists..." ) );
		
		//Retrieve the list of update lists
		remoteToLocal(
			"HashList.php?Base=dev&R=Y&File=lists/Lists.txt",
			"Lists.txt"
		);
		
		//TODO
		//1. If Lists.txt is empty, something went wrong and exit gracefully
		//2. Use TempFile to download, if problem use old.
		
		//0%
		setProgressValue( NUM_OVERALLPB, 0, 1, 0 );
		
		//Get the sub lists that have the different modules.
		handleUpdateList( "Lists.txt", "HashList.php?Base=dev&File=", 1, 0 );
		
		//Do the updating
		final String[] subLists = getLocalList( "Lists.txt" );
		int subListNum = 0;
		for ( String list : subLists )//Move through the sublists
		{
			String listname = null;
			
			{
				final String[] linea = list.split( ";" );
				if ( linea.length != 2 )
				{
					//Invalid Line
					updateError(
						localize(
							"Error_HashListLine1",
							"Encountered an invalid line in the Hash List: "
						)
						+ list,
						null
					);
				}
				else
				{
					listname = linea[0];
				}
			}
			
			updated = handleUpdateList(
				listname,
				"dev/",
				subLists.length,
				subListNum
			);
			
			subListNum++;
			
			//100% divided into NumberOfList Pieces, this is piece subListNum
			setProgressValue(
				NUM_OVERALLPB,
				0,
				HUNDRED_PERCENT,
				( HUNDRED_PERCENT / subLists.length * subListNum )
			);
			
			if ( updated ) //If we did something... restart.
			{
				for ( int i = UPDATE_RESTART_DELAY; i > 0; --i )
				{
					setSplashText(
						localize(
							"Updated2",
							"Module Updated."
						) + " (" + i + ")"
					);
					
					sleep( ONE_SECOND );
				}
				restart( "BC" );
				throw new ThreadDeath();
			}
		}
		
		//100% Complete
		setProgressValue( NUM_OVERALLPB, 0, 1, 1 );
	}

	/**
	 * Loads localization information if available.
	 */
	private void loadLocalization()
	{
		final Locale userLocale = new Locale(
			SETTINGS.getProperty( "locale" )
		);
		try
		{
			final ClassLoader myClassLoader =
				net.sf.backcomp.utils.BC.class.getClassLoader();
			
			URLClassLoader myURLClassLoader = null;
			
			try
			{
				myURLClassLoader =
					new URLClassLoader(
						new URL[]
						{
							new File( "." + File.separator ).toURI().toURL(),
						},
						myClassLoader
					);
				sLanguageBundle =
					ResourceBundle.getBundle(
							"Root",
							userLocale,
							myURLClassLoader
					);
			}
			catch ( MalformedURLException ex )
			{
				//!Don't Localize
				showError(
						"Problem loading localization.\n\n"
						+ makeStackTrace( ex )
				);
			}
		}
		catch ( MissingResourceException e )
		{
			//!Don't Localize
			System.out.println( "No locaLizations found." );
		}
		
		if (
				sLanguageBundle != null
				&& !userLocale.equals( sLanguageBundle.getLocale() )
		)
		{
			//Propose using different language
			
			final File dir = new File( "." );
			
			//Get all files
			String[] children = dir.list();
			
			//If we found some (I hope so!)
			if ( children.length > 0 )
			{
				//Look for language bundles starting with root_
				final ArrayList<String> filtered = new ArrayList<String>();
				for ( int i = 0; i < children.length; ++i )
				{
					if ( children[i].startsWith( "Root_" ) )
					{
						filtered.add( children[i] );
					}
				}
				
				//Put us back in an array
				children = filtered.toArray( new String[filtered.size()] );
				
				//List other languages.
				if ( children.length > 0 )
				{
					System.out.println( "Other Languages Found:" );
					for ( int i = 0; i < children.length; ++i )
					{
						System.out.println( children[i] );
					}
					
					
					showMsg(
						"Other Lanaguages.",
						"DEBUG",
						JOptionPane.INFORMATION_MESSAGE
					);
					//TODO:Ask about language?
				}
				else
				{
					//!Don't Localize
					System.out.println( "No Languages." );
				}
			}
		}
	}

	/**
	 * Loads application settings.
	 */
	private void loadSettings()
	{
		FileInputStream settingsInputStream = null;
		try
		{
			settingsInputStream = new FileInputStream( "Settings.properties" );
			SETTINGS.load( settingsInputStream );
		}
		catch ( FileNotFoundException ex )
		{
			//Didn't find settings file
			//Defaults
			//!Don't Localize
			System.out.println( "No Settings File" );
		}
		catch ( IOException ex )
		{
			//Defaults
			//!Don't Localize
			System.out.println( "IO Error loading file" );
		}
		finally
		{
			if ( settingsInputStream != null )
			{
				try
				{
					settingsInputStream.close();
				}
				catch ( IOException ex )
				{
					//!Don't Localize
					final String defErrMsg =
						"Problem occurred while trying to close"
						+ "settings file handle.";
					final String details = "\n\n" + makeStackTrace( ex );
					showError( defErrMsg + details );
				}
			}
		}
	}
	
	
	
	/**
	 * Runs through an update list updating each item.
	 * 
	 * @param listname List file name
	 * @param prefix Download prefix
	 * @param numLists Number of update lists
	 * @param listNum This list Number
	 * @return Whether files were updated
	 */
	private boolean handleUpdateList(
		String listname,
		String prefix,
		int numLists,
		int listNum )
	{
		//Vars
		boolean updated = false;
		int fileNum = 0;
		
		
		final String[] lines = getLocalList( listname );
		for ( String line : lines )//Move through the files
		{
			String name = null;
			String hash = null;
			
			{
				final String[] linea = line.split( ";" );
				if ( linea.length != 2 )
				{
					//ERROR
					updateError(
						localize(
							"Error_HashListLine1",
							"Encountered an invalid line in the Hash List: "
						) + line,
						null
					);
					//NEVER RETURNS.
				}
				else
				{
					name = linea[0];
					hash = linea[1];
				}
			}
			
			setSplashText( " " + localeFormat( "Checking1", name ) );
			
			if ( getLocalHash( name ).compareTo( hash ) != 0 )
			{
				updated = true;
				setSplashText( " " + localeFormat( "Downloading1", name ) );
				
				if ( !remoteToLocal( prefix + name, "Download.tmp" ) )
				{
					updateError(
						localeFormat(
							"Error_Download1",
							name
						),
						null
					);
				}
				File src = new File( "Download.tmp" );
				
				//Verify it
				if ( getLocalHash( "Download.tmp" ).compareTo( hash ) != 0 )
				{
					//Data ERROR
					updateError(
						localeFormat(
							"Error_Download2",
							new String[]
							{
								name,
								getLocalHash( name ),
								hash
							}
						),
						null
					);
				}
				
				//Make the slash char for this OS
				name = name.replace( '/', File.separatorChar );
				
				final int index = name.lastIndexOf( File.separatorChar );
				if ( index != -1 )
				{
					final File folder = new File( name.substring( 0, index ) );
					if ( !folder.exists() )
					{
						if ( !folder.mkdirs() )
						{
							updateError(
								localeFormat(
									"Error_MKDir1",
									name.substring( 0, index )
								),
								null
							);
						}
					}
				}
				
				File dest = new File( name );
				if ( !dest.exists() || dest.delete() )
				{
					if ( src.renameTo( dest ) )
					{
						setSplashText( localeFormat( "Updated1", name ) );
					}
					else
					{
						updateError(
							localeFormat(
								"Error_Rename1",
								name
							),
							null
						);
					}
				}
				else
				{
					updateError( localeFormat( "Error_Delete1", name ), null );
				}
			}
		
			
			fileNum++;
			
			setProgressValue(
				NUM_OVERALLPB,
				0,
				HUNDRED_PERCENT,
				//is the big section for each list
				HUNDRED_PERCENT / numLists * listNum
				//divides a big section into the number of files
				+ ( HUNDRED_PERCENT / numLists ) / lines.length * fileNum
			);
			
		}
		
		return updated;
	}
	
	
	
	
	/**
	 * Text to be shown on splash dialog
	 * 
	 * @param text Text to be displayed on splash.
	 */
	private void setSplashText( String text )
	{
		publish( new Object[] {text} );
	}
	
	
	
	
	/**
	 * Sets the Progress Bar Value
	 *
	 * @param Bar Bar number, Use the constants prefixed with NUM_
	 * @param Min Minimum value for bar
	 * @param Max Maximum value for bar
	 * @param Val The value for the bar
	 */
	private void setProgressValue(
		Integer Bar,
		Integer Min,
		Integer Max,
		Integer Val )
	{
		publish( new Object[] {Bar, Min, Max, Val} );
	}
	
	
	
	
	/**
	 * Sets the Progress Bar to an indeterminate state
	 *
	 * @param Bar Bar number, Use the constants prefixed with NUM_
	 */
	private void setProgressUnknown( Integer Bar )
	{
		publish(
			new Object[]
			{
				Bar, //Which Progress Bar
				Integer.valueOf( 0 ), //Minimum 0
				Integer.valueOf( -1 ), //Unknown
				Integer.valueOf( 0 ) //Current 0
			}
		);
	}
	
	
	
	
	/**
	 * Updates the splash dialog
	 * 
	 * @param chunks A List of queued items to update in order.
	 */
	@Override
	protected void process( List<Object[]> chunks )
	{
		//Handle each item in order
		for ( Object row[] : chunks )
		{
			switch ( row.length )
			{
			case 0: //Create Splash Directive
				if ( mSplashFrame == null ) //Is splash in existence?
				{
					//Create
					createAndShowGUI();
				}
				break;
			
			case 1: //Text Update
				if ( row[0] instanceof String )
				{
					mSplashText.setText( (String) row[0] );
				}
				break;
			
			case 4: //Progress Bar Update
				//Bar, Min, Max, Val (Max is -1 for Indeterminate)
				if (
						row[0] instanceof Integer
						&& row[1] instanceof Integer
						&& row[2] instanceof Integer
						&& row[3] instanceof Integer
				)
				{
					if ( mSplashProgressBars[(Integer) row[0]] != null )
					{
						if ( ( (Integer) row[2] ) == -1 )
						{
							//Unknown time
							mSplashProgressBars[(Integer) row[0]]
								.setIndeterminate( true );
						}
						else
						{
							//Specific Size
							processProgressValue(
								mSplashProgressBars[(Integer) row[0]],
								(Integer) row[1],
								(Integer) row[2],
								(Integer) row[3]
							);
						}
					}
				}
				break;
			default:
			} //End Switch
		} //End For
	}
	
	/**
	 * Sets the Progress Bar to certain progress.
	 * 
	 * @param Com Progress Bar Component
	 * @param Min Minimum Value
	 * @param Max Maximum Value
	 * @param Val Current Value
	 */
	private void  processProgressValue(
			JProgressBar Com,
			int Min,
			int Max,
			int Val
	)
	{
		Com.setMinimum( Min );
		Com.setMaximum( Max );
		Com.setValue( Val );
		Com.setIndeterminate( false );
	}
	
	/**
	 * Creates and displays the splash window.
	 */
	private void createAndShowGUI()
	{
		//Create and set up the window.
		mSplashFrame = new JWindow();
		mSplashFrame.setSize( SPLASH_WIDTH, SPLASH_HEIGHT );
		
		//The main pane
		final JPanel mainPane = new JPanel();
		mainPane.setLayout( new BorderLayout() );
		
		//Images are transparent, setup the background
		mainPane.setBackground( Color.BLACK );
		
		//Add the background Image
		final JLabel logo =
			new JLabel(
				new ImageIcon(
					"images" + File.separator + "Logo.png",
					"Updater"
				)
			);
		mainPane.add( logo, BorderLayout.WEST );
		
		//New pane for title and progress
		final JPanel titlePane = new JPanel();
		titlePane.setLayout( new BorderLayout() );
		titlePane.setBackground( Color.BLACK );
		
		//Title Image
		final JLabel title =
			new JLabel(
				new ImageIcon(
					"images" + File.separator + "Title.png",
					"Updater"
				)
			);
		titlePane.add( title, BorderLayout.NORTH );
		
		//Create the Progress Stuff
		final JPanel south = new JPanel();
		south.setLayout( new BorderLayout() );
		south.setBackground( Color.BLACK );
		
		//Progress Text
		//Don't Localize
		mSplashText = new JLabel( " Loading Config..." );
		mSplashText.setForeground( Color.WHITE );
		
		//Progress Bar, Duh
		mSplashProgressBars[NUM_ITEMPB] = new JProgressBar();
		mSplashProgressBars[NUM_OVERALLPB] = new JProgressBar();
		
		//when the task of (initially) unknown length begins:
		mSplashProgressBars[NUM_ITEMPB].setIndeterminate( true );
		
		//Put it where it goes
		south.add( mSplashText, BorderLayout.NORTH );
		south.add( mSplashProgressBars[NUM_ITEMPB], BorderLayout.CENTER );
		south.add( mSplashProgressBars[NUM_OVERALLPB], BorderLayout.SOUTH );
		
		titlePane.add( south, BorderLayout.SOUTH );
		
		mainPane.add( titlePane, BorderLayout.CENTER );
		
		//Main pane for window
		mSplashFrame.setContentPane( mainPane );
		
		
		//frame.pack();
		
		//Center frame
		final Dimension screenSize =
			Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension size = mSplashFrame.getSize();
		screenSize.height = screenSize.height / 2;
		screenSize.width = screenSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		final int y = screenSize.height - size.height;
		final int x = screenSize.width - size.width;
		mSplashFrame.setLocation( x, y );
		
		//Display the window.
		mSplashFrame.setVisible( true );
	}
	
	/**
	 * Make sure the splash is gone when terminating.
	 */
	@Override
	protected void done()
	{
		if ( mSplashFrame != null )
		{
			//Destroy
			mSplashFrame.dispose();
			mSplashFrame = null;
		}
	}
	
	//
	//END Swing Worker Meat & Potato's
	//
	
	
	
	
	
	//
	//Gravy
	//
	
	/**
	 * Localizes text based on a template.
	 * 
	 * @param template Localization Template
	 * @param defaultText Text to use if localized not available.
	 * @return Localized text
	 */
	private static String localize( String template, String defaultText )
	{
		
		if ( sLanguageBundle != null )
		{
			try
			{
				return sLanguageBundle.getString( template );
			}
			catch ( Exception e )
			{
				//Template missing
				return "~" + defaultText;
			}
		}
		else
		{
			//No Locale
			return "#" + defaultText;
		}
	}
	
	/**
	 * Localizes text based on a template with the
	 * pre-localized argument provided.
	 * 
	 * @param template Localization Template
	 * @param Arg Argument to insert into the localization.
	 * @return Localized text
	 */
	private static String localeFormat( String template, String Arg )
	{
		return localeFormat( template, new String[]{Arg} );
	}
	
	/**
	 * Localizes text based on a template with the
	 * pre-localized arguments provided.
	 * 
	 * @param template Localization Template
	 * @param Args Arguments to insert into the localization.
	 * @return Localized text
	 */
	private static String localeFormat( String template, String[] Args )
	{
		
		if ( sLanguageBundle != null )
		{
			Locale currentLocale = null;
			if ( SETTINGS.getProperty( "locale" ) != null )
			{
				currentLocale = new Locale( SETTINGS.getProperty( "locale" ) );
			}
			else
			{
				currentLocale = new Locale( "en" );
			}
			
			String stringTemplate = null;
			
			try
			{
				stringTemplate = sLanguageBundle.getString( template );
			}
			catch ( NullPointerException ex )
			{
				return " **Null Localization Template Name** ";
			}
			catch ( MissingResourceException ex )
			{
				return " **Localization Template '"
					+ template + "' Does Not Exist** ";
			}
			catch ( Exception ex )
			{
				return " **Unknown Localization Problem** ";
			}
			
			final MessageFormat formatter =
				new MessageFormat(
					stringTemplate,
					currentLocale
				);
			return formatter.format( Args );
		}
		else
		{
			return " **No Locale Availible** ";
		}
	}
	
	/**
	 * Gets size of file on Remote Server (if available) relative to SERVER_PATH
	 * @param file Filename
	 * @return size in bytes
	 */
	private static int getRemoteSize( String file )
	{
		try
		{
			//Create URL
			final URL path =
				new URL( SETTINGS.getProperty( "server_path" ) + file );
			
			//Connect
			final URLConnection myURLConnection = path.openConnection();
			myURLConnection.connect();
			//Get Size
			return myURLConnection.getContentLength();
		}
		catch ( MalformedURLException ex )
		{
			//Malformed Path, SERVER_PATH wrong?
			return -1;
		}
		catch ( IOException ex )
		{
			return -1; //God only knows what went wrong.
		}
	}
	
	/**
	 * Gets a list from a file
	 * @param file File to get list from
	 * @return Each line from file in an array
	 */
	private static String[] getLocalList( String file )
	{
		final ArrayList<String> data = new ArrayList<String>();
		try
		{
			//Prepare the buffers for reading
			final BufferedReader in =
				new BufferedReader(
					new FileReader(
						new File(
							file
						)
					)
				);
			//Read it...
			
			String inputLine;
			while ( ( inputLine = in.readLine() ) != null )
			{
				data.add( inputLine );
			}
			
			in.close();
		}
		catch ( IOException ex )
		{
			//File doesn't exist or is unavailable.
			//Return empty list
			ex = null;
		}
		return data.toArray( new String[] {} );
	}
	
	/**
	 * Computes the Hex MD5 Hash of a file on the local file system.
	 * @param file Path to file on local file system.
	 * @return Hash string
	 */
	private static String getLocalHash( String file )
	{
		//Byte array because JAVA returns Binary
		byte[] res;
		
		FileInputStream is = null;
		
		try
		{
			//Get Binary Hash
			final MessageDigest md5 = MessageDigest.getInstance( "MD5" );
			
			final File f = new File( file );
			
			is = new FileInputStream( f );
			final byte[] buffer = new byte[16 * ONE_KILO];
			int read = 0;
			
			while ( ( read = is.read( buffer ) ) > 0 )
			{
				md5.update( buffer, 0, read );
			}
			
			res = md5.digest();
		}
		catch ( NoSuchAlgorithmException ex )
		{
			showError( localize( "Error_MD51", "MD5 Algorithm missing" ) );
			throw new ThreadDeath();
		}
		catch ( FileNotFoundException ex )
		{
			return ""; //File not found...
		}
		catch ( IOException ex )
		{
			return ""; //God only knows what went wrong.
		}
		finally
		{
			if ( is != null )
			{
				try {
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return toHex( res ); //Convert Bin to Hex
	}
	
	/**
	 * Do a file transfer.
	 * @param sFile Source file on server
	 * @param dFile Destination file locally
	 * @return Success or Failure
	 */
	private boolean remoteToLocal( String sFile, String dFile )
	{
		int progressBarValue = 0;
		BufferedInputStream bis = null;
		BufferedOutputStream fos = null;
		final int remoteSize = getRemoteSize( sFile );
		
		//Set Progress Bar State to Empty
		setProgressValue( NUM_ITEMPB, 0, 1, 0 );
		
		try
		{
			//Create URL.
			final URL url =
				new URL( SETTINGS.getProperty( "server_path" ) + sFile );
			bis = new BufferedInputStream( url.openStream(), ONE_KILO );
			
			dFile = dFile.replace( '/', File.separatorChar ); //Make for this OS
			
			final int index = dFile.lastIndexOf( File.separatorChar );
			if ( index != -1 )
			{
				if ( !new File( dFile.substring( 0, index ) ).mkdirs() )
				{
					updateError(
						localeFormat(
							"Error_MKDir1",
							dFile.substring( 0, index )
						),
						null
					);
				}
			}
			
			final File file = new File( dFile );
			
			fos = new BufferedOutputStream(
				new FileOutputStream( file ),
				4 * ONE_KILO
			);
			
			final byte[] buffer = new byte[ONE_KILO];
			
			int count;
			while ( ( count = bis.read( buffer ) ) != -1 )
			{
				fos.write( buffer, 0, count );
				progressBarValue += count;
				
				//Set progress bar to amount downloaded
				setProgressValue( NUM_ITEMPB, 0, remoteSize, progressBarValue );
			}
			
		}
		catch ( MalformedURLException ex )
		{
			updateError(
				localize(
					"Error_Download3",
					"Server URL malformed. The server may be incorrect."
				),
				ex
			);
			return false;
		}
		catch ( IOException ex )
		{
			updateError(
				localize(
					"Error_Download4",
					"Problem occurred while trying to download."
				),
				ex
			);
			return false;
		}
		finally
		{
			if ( fos != null )
			{
				try
				{
					fos.close();
				}
				catch ( IOException ex )
				{
					final String defErrMsg =
						"Problem occurred while trying to download.";
					final String details = "\n\n" + makeStackTrace( ex );
					showError(
						localize( "Error_Download4", defErrMsg )
						+ details
					);
				
				}
			}
				
			if ( bis != null )
			{
				try
				{
					bis.close();
				}
				catch ( IOException ex )
				{
					final String defErrMsg =
						"Problem occurred while trying to download.";
					final String details = "\n\n" + makeStackTrace( ex );
					showError(
						localize( "Error_Download4", defErrMsg )
						+ details
					);
				
				}
			}
		}
		
		//Reset
		setProgressUnknown( NUM_ITEMPB );
		return true;
	}
	
	
	
	
	/**
	 * Displays Error Message
	 * 
	 * @param msg Message
	 */
	private static void showError( String msg )
	{
		showMsg( msg, "Error", JOptionPane.ERROR_MESSAGE );
	}
	
	
	
	
	/**
	 * Displays Message
	 * 
	 * @param msg Message
	 * @param title Title to display
	 * @param icon JOptionPane icon
	 */
	private static void showMsg( String msg, String title, int icon )
	{
		JOptionPane.showMessageDialog( null, msg, title, icon );
	}
	
	/**
	 * Never Returns Successfully.
	 * 
	 * Tries restarting and if it doesn't help it displays an error and bails.
	 * 
	 * @param msg Message in error if already restarted once.
	 * @param ex Exception that started it all.
	 */
	private void updateError( String msg, Exception ex )
	{
		if ( SETTINGS.getProperty( "updateError" ).equalsIgnoreCase( "False" ) )
		{
			setSplashText(
				localize(
					"Error_Update1",
					"Error while updating. "
				)
			);
			
			try
			{
				SETTINGS.setProperty( "updateError", "True" );
				SETTINGS.store(
					new FileOutputStream( "Settings.properties" ),
					"Background Compute"
				);
				
				for ( int i = ERROR_RESTART_DELAY; i > 0; --i )
				{
					setSplashText(
						localize(
							"Error_Update1",
							"Error while updating."
						)
						+ " (" + i + ")"
					);
					
					sleep( ONE_SECOND );
				}
				
				restart( "BC" );
			}
			catch ( Exception e )
			{
				final String defErrMsg =
					"Error while updating. Unable to try again."
					+ "\n\nRestart Background Compute, "
					+ "If this does not resolve the problem contact"
					+ "Technical Support.\n\nError: ";
				final String details =
					msg
					+ (
						( ex != null )
						? ( "\n\n" + makeStackTrace( ex ) )
						: ""
					)
					+ (
						( e != null )
						? (
							"\n\nUnable to save settings trace:\n"
							+ makeStackTrace( e )
						)
						: "" );
				showError(
					localize( "Error_UpdateSave1", defErrMsg )
					+ details
				);
			}
		}
		else
		{
			final String defErrMsg =
				"Error while updating. Retrys Failed."
				+ "\n\nContact Technical Support.\n\nError: ";
			final String details =
				msg
				+ (
					( ex != null )
					? ( "\n\n" + makeStackTrace( ex ) )
					: ""
				);
			showError( localize( "Error_Update2", defErrMsg ) + details );
		}
		
		//Nuke The Thread.
		throw new ThreadDeath();
	}
	
	
	
	
	//
	//NEW VM
	
	/**
	 * Runs a class in a new Virtual Machine that looks the same as this VM
	 * 
	 * @param ClassName Name of class to run
	 */
	static void restart( String ClassName )
	{
		restart( ClassName, "" );
	}
	
	/**
	 * Restart Program (Thanks to the makers of JAP)
	 * @param ClassName Name of class to run
	 * @param App To tell the truth, I don't know
	 */
	static void restart( String ClassName, String App )
	{
		String classPath = "";
		if ( CLASS_PATH.indexOf( ';' ) > 0 )
		{
			classPath =
				CLASS_PATH.substring( 0, CLASS_PATH.indexOf( ';' ) )
				+ App
				+ CLASS_PATH.substring( CLASS_PATH.indexOf( ';' ) );
		}
		else
		{
			classPath = CLASS_PATH + App;
		}
		
		// restart command
		String strRestartCommand = "";
		
		//what is used: sun.java or JView?
		final String strJavaVendor = System.getProperty( "java.vendor" );
		//System.out.println("Java vendor: " + strJavaVendor);
		
		String javaExe = null;
		String pathToJava = null;
		if ( strJavaVendor.toLowerCase().indexOf( "microsoft" ) != -1 )
		{
			
			pathToJava = System.getProperty( "com.ms.sysdir" ) + File.separator;
			javaExe = "jview /cp";
		}
		else
		{
			pathToJava =
				System.getProperty( "java.home" ) + File.separator
				+ "bin" + File.separator;
			javaExe = "javaw -cp"; // for windows
		}
		strRestartCommand =
			pathToJava + javaExe + " \"" + classPath + "\" " + ClassName;
			// + m_commandLineArgs;
		
		
		
		try
		{
			Runtime.getRuntime().exec( strRestartCommand );
		}
		catch ( Exception ex )
		{
			javaExe = "java -cp"; // Linux/UNIX
			strRestartCommand =
				pathToJava
				+ javaExe + " \"" + classPath + "\" " + ClassName;
				// + m_commandLineArgs;
			
			try
			{
				Runtime.getRuntime().exec( strRestartCommand );
			}
			catch ( Exception e )
			{
				showError(
					localize(
						"Error_Restart1",
						"Error while trying to restart Background Compute."
						+ "\n\nError: "
					)
					+ ex
				);
			}
		}
	}
	
	/**
	 * Return Class Path for update (Thanks to the makers of JAP)
	 * 
	 * @return class path of the VM
	 */
	private static String getClassPath()
	{
		try
		{
			return System.getProperty( "java.class.path" );
		}
		catch ( SecurityException e )
		{
			return "";
		}
	}
	
	/**
	 * Generates a stack-trace as a string.
	 *
	 * @param aThrowable Throwable object to generate stack trace from
	 * @return "Stack Trace: <code>stacktrace</code>"
	 */
	public static String makeStackTrace( Throwable aThrowable )
	{
		//add the class name and any message passed to constructor
		final StringBuilder result = new StringBuilder( "Stack Trace: " );
		result.append( aThrowable.toString() );
		
		result.append( NEW_LINE );
		
		//add each element of the stack trace
		for ( StackTraceElement element : aThrowable.getStackTrace() )
		{
			result.append( element );
			result.append( NEW_LINE );
		}
		return result.toString();
	}
	
	/**
	 * Sleeps for specified period. May return sooner if thread is interrupted.
	 * 
	 * @param ms Milliseconds to sleep
	 */
	private static void sleep( long ms )
	{
		try
		{
			Thread.sleep( ms );
		}
		catch ( InterruptedException ex )//
		{
			return;
		}
	}
	
	/**
	 * Retrieves the resource bundle for localizing to the current language.
	 * 
	 * @return Language Localization Bundle
	 */
	static ResourceBundle getLanguageBundle()
	{
		return sLanguageBundle;
	}
	
	
//////////
//
//HEX
//
//////////

/*
 * Look - it's a disclaimer!
 *
 * Copyright (c) 1996 Widget Workshop, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted, provided that this copyright notice is kept 
 * intact. 
 * 
 * WIDGET WORKSHOP MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WIDGET WORKSHOP SHALL NOT BE LIABLE
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  WIDGET WORKSHOP
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 *
 * What won't those crazy lawyers think up next?
 * 
 * Modified by: Deatbob
 * 
 */
	
	/**
	 * Converts a byte to a two character string. 
	 * 
	 * @param b The bytes to be converted.
	 * @category HEX
	 * 
	 * @return Hex String
	 */
	private static String toHex( byte[] b )
	{
		return toHex( b, b.length );
	}
	
	/**
	 * Converts a byte to a two character string. 
	 * 
	 * @param b The bytes to be converted.
	 * @param len Number of bytes to be converted.
	 * @category HEX
	 * 
	 * @return Hex String
	 */
	private static String toHex( byte[] b, int len )
	{
		final StringBuffer s = new StringBuffer( "" );
		int i;
		
		if ( b == null )
		{
			return null; //return null on invalid
		}
		
		for ( i = 0; i < len; i++ )
		{
			s.append( toHex( b[i] ) );
		}
		
		return s.toString();
	}
	
	/**
	 * Converts a byte to a two character string. 
	 * 
	 * @param b The byte to be converted.
	 * @category HEX
	 * 
	 * @return Two character string
	 */
	private static String toHex( byte b )
	{
		final int i = b & 0xFF;
		final String s = Integer.toHexString( i );
		return ( s.length() == 2 ) ? s : ( "0" + s );
	}
	
	//END HEX
}
