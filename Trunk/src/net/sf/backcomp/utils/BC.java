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
 
import javax.swing.*;

import java.awt.*;

import java.util.*;
import java.text.*;//String Formatting

import java.io.*;//File

import java.net.*; //URL

import java.security.*; //For MD5

import java.util.List;

/**
 * The Loader for Background Compute
 * 
 * 
 * @author Deathbob
 * @version 0.2 2008/01/25
 *
 */
public final class BC extends SwingWorker<Object,Object[]>
{
	private BC(){}//Only this class can create an instance.
	
	//Private Init's
	
	private static Properties defaultSettings()
	{
		Properties set = new Properties();
		
		set.setProperty("cpu_limit", "80");
		set.setProperty("locale", "en");
		set.setProperty("server_path", "http://defcon1.hopto.org/updates/bc/");
		set.setProperty("updateError", "False");
		
		return set;
	}
	
	//Private Variables
	
	private static JLabel		Text = null;			//Splash Text Line
	private static JProgressBar	PB = null;				//Splash Progress Bar
	private static JProgressBar	OverallPB = null;		//Overall Progress Bar
	private static JWindow		frame = null;			//The splash frame
	private static boolean		SplashCreated = false;	//Is the splash created?
	
	//Package Variables
	
	final static Properties Settings = new Properties( defaultSettings() );//Load settings object with defaults
	static ResourceBundle LTextRB = null;
	
	//Functions
	
	@Override
	protected Object doInBackground()
	{
		createAndShowGUI();
		return new Object();
	}
	
    private void createAndShowGUI()
    {
        publish( new Object[] {} ); //Display the Splash Screen
        
        try{
        	Settings.load( new FileInputStream("Settings.properties") );
        }
        catch(FileNotFoundException ex)
        {
        	//Didn't find settings file
        	//TODO:Ask about language?
        	
        	/*
        	File dir = new File("directoryName");

    
		    // It is also possible to filter the list of returned files.
		    FilenameFilter filter = new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		            return name.startsWith("root_");
		        }
		    };
		    String[] children = dir.list(filter);
		    
        	 */
        	
        	//Don't Localize
        	System.out.println("No Settings File");
        }
        catch(IOException ex)
        {
        	//Don't Localize
        	System.out.println("IO Error loading file");
        }    
        
        //Just a sub scope while we make the ResourceBundle
        {
        	Locale UserLocale = new Locale( Settings.getProperty("locale") );
        	try
        	{
        		ClassLoader CL = net.sf.backcomp.utils.BC.class.getClassLoader();
        		
        		URLClassLoader UCL = null;
        		
        		try
        		{
        			UCL = new URLClassLoader(new URL[]{new File("." + File.separator).toURI().toURL()},CL);
        		}
        		catch(MalformedURLException ex)
        		{
        			//Don't Localize
        			JOptionPane.showMessageDialog(null,"Current Dir Path Malformed. You may see odd behaviour.\nContact Support.","Error",JOptionPane.ERROR_MESSAGE);
        		}
        		LTextRB = ResourceBundle.getBundle("Root",UserLocale,UCL);
        	}
        	catch(MissingResourceException e)
        	{
        		//Don't Localize
        		System.out.println("Failed to open language bundle.");
        	}
        }
         
        try{
        	Settings.store( new FileOutputStream("Settings.properties") , "Background Compute" );
        }
        catch(Exception ex)
        {
        	JOptionPane.showMessageDialog(null,"Unable to save settings. You may see odd behaviour.\nContact Support.","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        publish( new Object[] {(Object)( " " + Localize("Loading1") ) } );
        //
        //BEGIN Updater
        //
        
        //**************************************************************
        //**************************************************************
        //**************************************************************
        
        boolean updated = false;
        
        publish( new Object[] {(Object)( " " + Localize("Lists1") ) } );//Text.setText( " " + LTextRB.getString("Lists1") );
    	remoteToLocal("HashList.php?Base=dev&R=Y&File=lists/Lists.txt", "Lists.txt");//, PB); //Retrieve the list of update lists
    	
    	//TODO
    	//1. If Lists.txt is empty, something went wrong and exit gracefully with notice
    	
    	if(OverallPB != null)
    	{
	    	OverallPB.setMinimum(0);
			OverallPB.setMaximum(100);
			OverallPB.setValue(0);
    	}
    	
    	//Get the sub lists that have the different modules.
    	for( String line : getLocalList("Lists.txt") )
        {
    		
        	String name = null;
        	String hash = null;
        		
        	{
        		String[] linea = line.split(";");
        		if(linea.length != 2)
        		{
        			//ERROR
        			//FIXME: Localize
        			UpdateError("Invalid Line in HashFiles HashList: " + line,null);
        			//NEVER RETURNS.
        		}
        		else
        		{
        			name = linea[0];
        			hash = linea[1];
        		}
        	}
        	
        	publish( new Object[] {(Object)( " " + LocaleFormat( "Lists2", new Object[] { name } ) ) } );
        	//Text.setText( " " + LocaleFormat( "Lists2", new Object[] { name } ) );
        	if( getLocalHash(name).compareTo( hash ) != 0 )
        	{
        		remoteToLocal("HashList.php?Base=dev&File=" + name,name);//, PB);
        	
        		if( getLocalHash(name).compareTo( hash ) != 0 )
        		{
        			//ERROR
        			//FIXME: Localize
        			PError("Failed to download updated hash list, Program Is In Inconsistant State.\n" + name + "\n" + getLocalHash(name) + "\n" + hash);
        			System.exit(-1);
        		}
        	}
        }
        
        //Do the updating
    	
        String[] SubLists = getLocalList("Lists.txt");
        int subListNum = 0;
    	for( String list : SubLists )//Move through the sublists
        {
        	String listname = null;
        		
        	{
        		String[] linea = list.split(";");
        		if(linea.length != 2)
        		{
        			//ERROR
        			//FIXME: Localize
        			UpdateError("Invalid Line in HashFiles HashList: " + list,null);
        			//NEVER RETURNS.
        		}
        		else
        		{
        			listname = linea[0];
        		}
        	}
        
        	int fileNum = 0;
        	String[] Lines = getLocalList(listname);
        	for( String line : Lines )//Move through the files
        	{
        		String name = null;
        		String hash = null;
        		
        		{
        			String[] linea = line.split(";");
        			if(linea.length != 2)
        			{
        				//ERROR
        				//FIXME: Localize
            			UpdateError("Invalid Line in HashList: " + line,null);
            			//NEVER RETURNS.
        			}
        			else
        			{
        				name = linea[0];
        				hash = linea[1];
        			}
        		}
        		
        		publish( new Object[] {(Object)( " " + LocaleFormat( "Checking1", new Object[] { name } ) ) } );

        		if( getLocalHash(name).compareTo( hash ) != 0 )
        		{
        			updated = true;
        			publish( new Object[] {(Object)( " " + LocaleFormat( "Downloading1", new Object[] { name } ) ) } );

        			if( !remoteToLocal(name,"Download.tmp"))//,PB) )
        			{
        				//FIXME:Localize
        				PError("Failed to download update, Program Is In Inconsistant State");
        				System.exit(-1);
        			}
        			File src = new File("Download.tmp");
        		
        			name = name.replace('/',File.separatorChar); //Make the char for this OS
        		
        			int index = name.lastIndexOf(File.separatorChar);
        			if(index != -1) new File(name.substring(0,index)).mkdirs();
        			
        			File dest = new File(name);
        			dest.delete();
        			if( src.renameTo( dest ) )
        			{
        				//FIXME: Need Localization.
        				publish( new Object[] {(Object)( " Updated File." ) } );
        				//Text.setText( " Moved." ); //*****************************************************
        			}
        			
        			
        		}
        	
        		
        		fileNum++;
            	if( OverallPB != null )
            		OverallPB.setValue(100/SubLists.length * subListNum + (100/SubLists.length)/Lines.length * fileNum);
        	}
        	subListNum++;
        	if( OverallPB != null )
        		OverallPB.setValue(100/SubLists.length * subListNum);
        	if(updated) //If we did something... restart.
    		{
    			restart("BC");
    			publish( new Object[] {} ); //Destroy the Splash Screen
    			System.exit(0);
    		}
        	try
        	{
        		Thread.sleep(1000);
        	}
        	catch(Exception ex){}
        	
        }
    	if(OverallPB != null)
    		OverallPB.setValue(100);
        //**************************************************************
        //**************************************************************
        //**************************************************************
        
        
        //
        //END Updater
        //
        
    	publish( new Object[] {} ); //Destroy the Splash Screen
		
        try{
        	Settings.setProperty("updateError", "False");
        	Settings.store( new FileOutputStream("Settings.properties") , "Background Compute" );
        }
        catch(Exception ex)
        {
        	//FIXME:Localize
        	JOptionPane.showMessageDialog(null,"Unable to save settings. You may see odd behaviour.\nContact Support.","Error",JOptionPane.ERROR_MESSAGE);
        }
    	
		//Start the main application
		javax.swing.SwingUtilities.invokeLater( new Mainapp() );
    }
    
    /**
     * Updates the splash dialog
     * 
     * @param chunks A List of queued items to update in order.
     */
    @Override
    protected void process(List<Object[]> chunks)
    {
    	//Handle each item in order
        for (Object row[] : chunks)
        {
        	//Create / Destroy Splash Directive
        	if(row.length == 0)
            {
            	if(SplashCreated)
            	{
            		//Destroy
            		frame.dispose();
            		frame = null;
            		SplashCreated = false;
            	
            	}
            	else
            	{
            		//Create
            		
            		//Create and set up the window.
                    frame = new JWindow();
                    frame.setSize(600,100);

                    //The main pane
                	JPanel mainPane = new JPanel();
                	mainPane.setLayout(new BorderLayout());
                	
                	//Images are transparent, setup the background
                	mainPane.setBackground(Color.BLACK);
                	
                	//Add the background Image
                    JLabel Logo = new JLabel(new ImageIcon("images" + File.separator + "Logo.png","Updater"));
                    mainPane.add(Logo,BorderLayout.WEST);
                    
                    //New pane for title and progress
                    JPanel titlePane = new JPanel();
                    titlePane.setLayout(new BorderLayout());
                    titlePane.setBackground(Color.BLACK);
                    
                    //Title Image
                    JLabel Title = new JLabel(new ImageIcon("images" + File.separator + "Title.png","Updater"));
                    titlePane.add(Title,BorderLayout.NORTH);
                    
            		//Create the Progress Stuff
            		JPanel South = new JPanel();
            		South.setLayout(new BorderLayout());
            		South.setBackground(Color.BLACK);
            		
            		//Progress Text
            		//Don't Localize
                	Text = new JLabel(" Loading Config...");
                	Text.setForeground(Color.WHITE);
                	
                	//Progress Bar, Duh
                	PB = new JProgressBar();
                	OverallPB = new JProgressBar();
                	
            		//when the task of (initially) unknown length begins:
            		PB.setIndeterminate(true);
            		
            		//Put it where it goes
            		South.add(Text,BorderLayout.NORTH);
            		South.add(PB,BorderLayout.CENTER);
            		South.add(OverallPB,BorderLayout.SOUTH);
            		
            		titlePane.add(South,BorderLayout.SOUTH);
                	
                	mainPane.add(titlePane,BorderLayout.CENTER);
                	
                	//Main pane for window
                	frame.setContentPane(mainPane);

                    
                    //frame.pack();
                    
                    //Center frame
            		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            		Dimension size = frame.getSize();
            		screenSize.height = screenSize.height/2;
            		screenSize.width = screenSize.width/2;
            		size.height = size.height/2;
            		size.width = size.width/2;
            		int y = screenSize.height - size.height;
            		int x = screenSize.width - size.width;
            		frame.setLocation(x, y);
                    
            		//Display the window.
                    frame.setVisible(true);
                    
                    SplashCreated = true;
            	}
            		
            }
        	else if(row.length == 1)
            {
        		//If 1 Item, it is text for progress
            	if(row[0] instanceof String)
            	{
            		Text.setText((String) row[0]);
            	}
            }
            else if(row.length == 3)
            {
            	//If 3 items, Progress Bar Update
            	//Min, Max, Val (Max is -1 for Indeterminate)
            	if(row[0] instanceof Integer && row[1] instanceof Integer && row[2] instanceof Integer)
            	{
            		if((Integer)row[1] == -1)
	            	{
            			//Unknown Length
                		PB.setIndeterminate(true);
	            	}
            		else
            		{
            			//Specific Size
            			PB.setMinimum((Integer)row[0]);
                		PB.setMaximum((Integer)row[1]);
                		PB.setValue((Integer)row[2]);
                		PB.setIndeterminate(false);
            		}
            	}
            	
            }
            
        }
    }

    public static void main(String[] args)
    {
    	try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	}catch(Exception e){} //If can't set native look and feel, oh well.
    	
    	//Start the self enclosed SwingWorker.
    	(new BC()).execute();
    	
    }
    
    /**
     * Localizes text based on a template.
     * 
     * @param template Localization Template
     * @return Localized text
     */
    static private String Localize(String template)
	{
		
		if(LTextRB!=null)
		{	
			try
			{
				return LTextRB.getString( template );
			}
			catch(Exception e)
			{
				//Hmm
				return " **Localization Template Error** ";
			}
		}
		else
		{
			return " **No Locale Availible** ";
		}
	}
    
    /**
     * Localizes text based on a template with the pre-localized arguments provided.
     * 
     * @param template Localization Template
     * @param Args Arguments to insert into the localization.
     * @return Localized text
     */
	static private String LocaleFormat(String template, Object[] Args)
	{
		
		if(LTextRB!=null)
		{	
			Locale CurrentLocale = null;
			if( Settings.getProperty("locale") != null )
			{
				CurrentLocale = new Locale( Settings.getProperty("locale") );
			}
			else
			{
				CurrentLocale = new Locale( "en" );
			}
			
			MessageFormat formatter = new MessageFormat( LTextRB.getString( template ), CurrentLocale );
			return formatter.format(Args);
		}
		else
		{
			return " **No Locale Availible** ";
		}
	}
    
    //Gets size of file on Remote Server (if availible) relative to SERVER_PATH
    static private int getRemoteSize(String file)
    {
    	try
    	{
    		//Create URL
    		URL path = new URL(Settings.getProperty("server_path") + file);
    		
    		//Connect
    		URLConnection UC = path.openConnection();
    		UC.connect();
    		//Get Size
    		return UC.getContentLength();
    	}
    	catch(MalformedURLException ex)
    	{
    		//Malformed Path, SERVER_PATH wrong?
    		return -1;
    	}
    	catch(IOException ex)
  		{
  			return -1; //God only knows what went wrong.
  		}
    }
    
    static private String[] getLocalList(String file)
    {
    	ArrayList<String> data = new ArrayList<String>();
		try
    	{
    		//Prepare the buffers for reading
    		BufferedReader in = new BufferedReader(	new FileReader( new File(file) ) );
    		//Read it...
 
    		String inputLine;
    		while ((inputLine = in.readLine()) != null)
    		{
    			data.add(inputLine);
    		}

			in.close();
    	}
    	catch(IOException ex)
    	{
    			//File doesn't exist or is unavailible.
    			//Return empty list
    	}		
    	return data.toArray(new String[]{});
    }
    
    /**
     * Computes the Hex MD5 Hash of a file on the local file system.
     * @param file Path to file on local file system.
     * @return Hash string
     */
    static private String getLocalHash(String file)
    {
    	//Byte array because JAVA returns Binary
    	byte[] res;
  		
  		try
  		{
  			//Get Binary Hash
  			MessageDigest md5 = MessageDigest.getInstance("MD5");
  		
  			File f = new File(file);
  			
  			int len;
    		byte[] msg = new byte[len = (int)f.length()]; 
    		FileInputStream fis = new FileInputStream(f); 
    		if (fis.read(msg) != len) return ""; //Failed to get data
    		
    		fis.close();
    	
    		md5.update(msg);
    		
    		res = md5.digest();
  		}
  		catch(NoSuchAlgorithmException ex)
  		{
  			//FIXME:Localize
  			PError("Missing MD5 Algorithm in runtime. It is required.");
  			System.exit(-1);
  			return ""; //MD5 Not availible (Odd)
  		}
  		catch(FileNotFoundException ex)
  		{
  			return ""; //File not found...
  		}
  		catch(IOException ex)
  		{
  			return ""; //God only knows what went wrong.
  		}
  		return toHexF(res);//Convert Bin to Hex
    }
    
    //Do a file transfer.
    private boolean remoteToLocal(String sFile, String dFile)//, JProgressBar PB)
    {
    	int PBVal = 0;
    	int RemoteSize = getRemoteSize(sFile);
    	
    	//Set Progress Bar State
    	publish(new Object[] {(Object)(Integer)0, (Object)(Integer)RemoteSize, (Object)(Integer)0});//Min, Max, Val (Max is -1 for Indeterminate)
    	/*if(PB != null)
    	{
    		PB.setMinimum(0);
    		PB.setMaximum(getRemoteSize(sFile));
    		PB.setValue(0);
    		PB.setIndeterminate(false);
    	}*/
    		
    	try
    	{ 
    		//Create URL.
			URL url = new URL(Settings.getProperty("server_path") + sFile);
			BufferedInputStream bis = new BufferedInputStream(url.openStream(), 1024);
			//TODO: Download to temp file then move.
			dFile = dFile.replace('/',File.separatorChar); //Make the char for this OS
    		
			int index = dFile.lastIndexOf(File.separatorChar);
			if(index != -1) new File(dFile.substring(0,index)).mkdirs();
			
			File file = new File(dFile);
			
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(file), 4*1024); 

			byte[] buffer = new byte[1024]; 

			int count;
			while ( ( count = bis.read(buffer) ) != -1)
			{ 
				fos.write(buffer,0,count);
				PBVal += count;
				
				publish(new Object[] {(Object)(Integer)0, (Object)(Integer)RemoteSize, (Object)(Integer)PBVal});//Min, Max, Val (Max is -1 for Indeterminate)
				//if(PB != null) PB.setValue(PBVal);
			} 
			
			fos.close();
			bis.close(); 
		}
		catch(MalformedURLException ex)
    	{
    		
    		//Malformed Path, SERVER_PATH wrong?
    		//Reset
			publish(new Object[] {(Object)(Integer)0, (Object)(Integer) (-1), (Object)(Integer)0});
			//if(PB != null) PB.setIndeterminate(true);
			return false;
    	}
		catch(IOException ioe)
		{
			//Reset
			publish(new Object[] {(Object)(Integer)0, (Object)(Integer) (-1), (Object)(Integer)0});
			//if(PB != null) PB.setIndeterminate(true);
			return false;
        }
        catch(Exception e)
        {
        	//Reset
        	publish(new Object[] {(Object)(Integer)0, (Object)(Integer) (-1), (Object)(Integer)0});
            //if(PB != null) PB.setIndeterminate(true);
            return false;
        }
        
        //Reset
        publish(new Object[] {(Object)(Integer)0, (Object)(Integer) (-1), (Object)(Integer)0});
        //if(PB != null) PB.setIndeterminate(true);
		return true;
    }
    
    /**
     * Displays Error Message
     * 
     * @param msg Message
     */
    static private void PError(String msg)
	{
		JOptionPane.showMessageDialog(null,msg,"Error",JOptionPane.ERROR_MESSAGE);
	}
    
    /**
     * Never Returns. Trys restarting and if doesn't help displays error and bails.
     * 
     * @param msg Message in error if already restarted once.
     */
    private void UpdateError(String msg, Exception ex)
    {
    	if(Settings.getProperty("updateError").equalsIgnoreCase("False"))
    	{
    		publish( new Object[] {(Object)( " " + Localize("UpdateError1") ) } );
	    	
    		try
    		{
            	Settings.setProperty("updateError", "True");
            	Settings.store( new FileOutputStream("Settings.properties") , "Background Compute" );
            	
	            try
		    	{
					Thread.sleep(3000);
		    	}
		    	catch(InterruptedException e){}
		    	
		    	restart("BC");
            }
            catch(Exception e)
            {
            	//FIXME:Localize Perror
            	JOptionPane.showMessageDialog(null,"Error while updating and unable to save settings. Bailing Out.\nCheck Permissions & Contact Support.","Error",JOptionPane.ERROR_MESSAGE);
            }
    	}
    	else
    	{
    		//FIXME:Localize PError
    		JOptionPane.showMessageDialog(null,"Error while updating. Bailing Out.\nContact Support.","Error",JOptionPane.ERROR_MESSAGE);
    	}
    	
    	throw new IllegalStateException("Update failed", ex);
    }
    
    
    
    //NEW VM
    
    static final String CLASS_PATH = getClassPath().trim(); //Class Path for restart
    
    static void restart(String ClassName)
    {
    	restart(ClassName, "");
    }
    
     //Restart Program (Thanks to the makers of JAP)
    static void restart(String ClassName,String App)//FIXME: Failed to work on Fedora 8
	{
		String classPath = "";
		if(CLASS_PATH.indexOf(';') > 0)
			classPath = CLASS_PATH.substring(0,CLASS_PATH.indexOf(';')) + App + CLASS_PATH.substring(CLASS_PATH.indexOf(';'));
		else
			classPath = CLASS_PATH + App;
		
		// restart command
		String strRestartCommand = "";

		//what is used: sun.java or JView?
		String strJavaVendor = System.getProperty("java.vendor");
		//System.out.println("Java vendor: " + strJavaVendor);

		String javaExe = null;
		String pathToJava = null;
		if (strJavaVendor.toLowerCase().indexOf("microsoft") != -1)
		{

			pathToJava = System.getProperty("com.ms.sysdir") + File.separator;
			javaExe = "jview /cp";
		}
		else
		{
			pathToJava = System.getProperty("java.home") + File.separator + "bin" + File.separator;
			javaExe = "javaw -cp"; // for windows
		}
		strRestartCommand = pathToJava + javaExe + " \"" + classPath + "\" " + ClassName;// + m_commandLineArgs;



	    try
		{
		    Runtime.getRuntime().exec(strRestartCommand);
			//System.out.println("Restart command: " + strRestartCommand);
		}
		catch (Exception ex)
		{
			javaExe = "java -cp"; // Linux/UNIX
			strRestartCommand = pathToJava + javaExe + " \"" + classPath + "\" " + ClassName;// + m_commandLineArgs;

			//System.out.println("JAP restart command: " + strRestartCommand);
			try
			{
				Runtime.getRuntime().exec(strRestartCommand);
			}
			catch (Exception a_e)
			{
				//FIXME: Localize
				PError("Error auto-restart: " + ex);
			}
		}
	}
	
	//Return Class Path for update (Thanks to the makers of JAP)
	private static String getClassPath()
	{
		try
		{
			return System.getProperty("java.class.path");
		}
		catch (SecurityException a_e)
		{
			return "";
		}
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
 * WIDGET WORKSHOP MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WIDGET WORKSHOP SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
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
 * What won't those crazy lawyers think up next? */
 
	static private String toHexF(byte[] b) { return toHexF(b, b.length); }
 
	static private String toHexF(byte[] b, int len)
	{
		StringBuffer s = new StringBuffer("");
		int i;

		if (b==null) return null;//return null on invalid
                    
		for (i=0; i<len; i++)
		{
			s.append(toHex(b[i]));
		}
 
		return s.toString();
	}
        
	static private String toHex(byte b)
	{
		Integer I = new Integer((((int)b) << 24) >>> 24);
		int i = I.intValue();
 
		if ( i < (byte)16 )
			return "0"+Integer.toString(i, 16);
		else
			return     Integer.toString(i, 16);
	}
	
	//END HEX
}
