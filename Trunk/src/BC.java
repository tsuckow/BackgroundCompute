/**
 * @(#)BPC.java
 *
 * BPC application
 *
 * @author 
 * @version 1.00 2006/12/20
 */
 
import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.text.*;//String Formatting

import java.io.*;//File

import java.net.*; //URL

import java.security.*; //For MD5
 
public class BC
{
	private static Properties defaultSettings()
	{
		Properties set = new Properties();
		
		set.setProperty("locale", "en");
		set.setProperty("update", "yes");
		set.setProperty("server_path", "http://defcon1.hopto.org/bc/");
		
		return set;
	}
	
	public final static Properties Settings = new Properties( defaultSettings() );//Load settings object with defaults
	public static ResourceBundle LTextRB = null;
	
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Updater");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setSize(300,140);
    	frame.setResizable(false);
    	frame.setUndecorated(true);

        //The content pane
    	JPanel mainPane = new JPanel();
    	mainPane.setLayout(new BorderLayout());
    	
    	mainPane.setBackground(Color.BLACK);
    	
    	//Add the background Image
        //ImageIcon icon = new ImageIcon("Splash.png","Boom");
        JLabel Logo = new JLabel(new ImageIcon("Splash.png","Updater"));
        mainPane.add(Logo,BorderLayout.CENTER);

		//Create the bottom
		JPanel South = new JPanel();
		South.setLayout(new BorderLayout());
		South.setBackground(Color.BLACK);
		
		//Progress Text
    	JLabel Text = new JLabel(" Loading Config...");
    	//ttest = Text;
    	Text.setForeground(Color.WHITE);
    	
    	//Progress Bar, Duh
    	JProgressBar PB = new JProgressBar();
		//when the task of (initially) unknown length begins:
		PB.setIndeterminate(true);
		
		South.add(Text,BorderLayout.CENTER);
		South.add(PB,BorderLayout.EAST);
    	
    	mainPane.add(South,BorderLayout.SOUTH);
    	
    	frame.setContentPane(mainPane);

        //Display the window.
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
        
        try
    	{
    		Thread.sleep(50);
    	}
    	catch(InterruptedException e)
    	{
    	}
        
        frame.setVisible(true);
        
        try
    	{
    		Thread.sleep(50);
    	}
    	catch(InterruptedException e)
    	{
    	}
        
        try{
        	Settings.load( new FileInputStream("Settings.properties") );
        }
        catch(FileNotFoundException ex)
        {
        	//Didn't find settings file
        	//Ask about language?
        	System.out.println("FNF");
        }
        catch(IOException ex)  { } //IO Exception!
        
        //Settings.setProperty("update","no");
        
        try{
        	Settings.store( new FileOutputStream("Settings.properties") , "Background Compute" );
        }
        catch(FileNotFoundException ex)
        {
        	//Didn't find settings file
        	//Ask about language?
        	System.out.println("FNF");
        }
        catch(IOException ex)  { } //IO Exception!
        
        Text.setText( "Loading Locale..." );
        
        //Just a sub scope while we make the ResourceBundle
        {
        	Locale UserLocale = new Locale( Settings.getProperty("locale") );
        	try
        	{
        		LTextRB = ResourceBundle.getBundle("Root",UserLocale);
        	}
        	catch(MissingResourceException e)
        	{
        		//We don't have any locales
        	}
        }
        
        //BPC.remoteToLocal("Title.fla","Test2.fla",PB);
        
        if(LTextRB!=null) Text.setText( " " + LTextRB.getString("Loading1") );
        
        //
        //BEGIN Updater
        //
        System.out.println("Update?");
        if(Settings.getProperty("update").equals("no"))
        {
        	System.out.println("Skip Update");
        	frame.dispose();
			frame = null;
			return;
        }
        
        boolean updated = false;
        
        for( String name : getRemoteList("UpdaterList.txt") )
        {
        	Text.setText( " " + LocaleFormat( "Checking1", new Object[] { name } ) );
        	if( getLocalHash(name).compareTo( getRemoteList("Hash.php?File=" + name)[0] ) != 0 )
        	{
        		updated = true;
        		Text.setText( " " + LocaleFormat( "Downloading1", new Object[] { name } ) );
        		remoteToLocal(name,"Download.tmp",PB);
        		File src = new File("Download.tmp");
        		
        		name = name.replace('/',File.separatorChar); //Make the char for this OS
        		
        		int index = name.lastIndexOf(File.separatorChar);
        		if(index != -1) new File(name.substring(0,index)).mkdirs();
        		
        		File dest = new File(name);
        		dest.delete();
        		if( src.renameTo( dest ) )
        		{
        			Text.setText( " Moved." ); //*****************************************************
        		}
        	}
        }
        if(updated)
    	{
    		restart("BC");
    		System.exit(0);
    	}
        
        for( String name : getRemoteList("MainList.txt") )
        {
        	Text.setText( " " + LocaleFormat( "Checking1", new Object[] { name } ) );
        	if( getLocalHash(name).compareTo( getRemoteList("Hash.php?File=" + name)[0] ) != 0 )
        	{
        		updated = true;
        		Text.setText( " " + LocaleFormat( "Downloading1", new Object[] { name } ) );
        		remoteToLocal(name,"Download.tmp",PB);
        		File src = new File("Download.tmp");
        		
        		name = name.replace('/',File.separatorChar); //Make the char for this OS
        		
        		int index = name.lastIndexOf(File.separatorChar);
        		if(index != -1) new File(name.substring(0,index)).mkdirs();
        		
        		File dest = new File(name);
        		dest.delete();
        		if( src.renameTo( dest ) )
        		{
        			Text.setText( " Moved." ); //*****************************************************
        		}
        	}
        }
        if(updated)
    	{
    		restart("BC");
    		System.exit(0);
    	}
        
        //
        //END Updater
        //
        
               
        try
    	{
    		Thread.sleep(200);
    	}
    	catch(InterruptedException e)
    	{
    	}
        
        frame.dispose();
		frame = null;  
    }

    public static void main(String[] args)
    {
    	try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	}catch(Exception e){} //If can't set native look and feel, oh well.
    	
    	//Place GUI in event thread.
		//javax.swing.SwingUtilities.invokeLater( new Runnable()
		//{
        //    	public void run()
        //    	{
                	createAndShowGUI();
        //		}
        //});
        
    	javax.swing.SwingUtilities.invokeLater( new mainapp() );
    }
    
	static public String LocaleFormat(String template, Object[] Args)
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
		if(LTextRB!=null)
		{	
			MessageFormat formatter = new MessageFormat( LTextRB.getString( template ), CurrentLocale );
			return formatter.format(Args);
		}
		else
		{
			return " **No Locale Availible** ";
		}
	}
    
    //Get file from remote website, relative to the server_path setting.
    static public String[] getRemoteList(String file)
    {
    	ArrayList<String> data = new ArrayList<String>();
    	try
    	{
    		//Create URL
    		URL path = new URL(Settings.getProperty("server_path") + file);
    		
    		try
    		{
    			//Prepare the buffers for reading
    			BufferedReader in = new BufferedReader(	new InputStreamReader( path.openStream() ) );
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
    			//Return empty array
    		}		
    	}
    	catch(MalformedURLException ex)
    	{
    		//Malformed Path, SERVER_PATH wrong?
    		//Return empty array
    	}
    	return data.toArray(new String[]{});
    }
    
    //Gets size of file on Remote Server (if availible) relative to SERVER_PATH
    static public int getRemoteSize(String file)
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
    
    static public String[] getLocalList(String file)
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
    
    //Get MD5 Hash as a hex string.
    static public String getLocalHash(String file)
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
    static public boolean remoteToLocal(String sFile, String dFile, JProgressBar PB)
    {
    	//Set Progress Bar State
    	if(PB != null)
    	{
    		PB.setMinimum(0);
    		PB.setMaximum(getRemoteSize(sFile));
    		PB.setValue(0);
    		PB.setIndeterminate(false);
    	}
    		
    	try
    	{ 
    		//Create URL.
			URL url = new URL(Settings.getProperty("server_path") + sFile);
			BufferedInputStream bis = new BufferedInputStream(url.openStream(), 1024);

			File file = new File(dFile);
			
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(file), 4*1024); 

			byte[] buffer = new byte[1024]; 

			int count;
			while ( ( count = bis.read(buffer) ) != -1)
			{ 
				fos.write(buffer,0,count);
				if(PB != null) PB.setValue(PB.getValue()+count);
			} 
			
			fos.close();
			bis.close(); 
		}
		catch(MalformedURLException ex)
    	{
    		
    		//Malformed Path, SERVER_PATH wrong?
    		//Reset
			if(PB != null) PB.setIndeterminate(true);
			return false;
    	}
		catch(IOException ioe)
		{
			//Reset
			if(PB != null) PB.setIndeterminate(true);
			return false;
        }
        catch(Exception e)
        {
        	//Reset
            if(PB != null) PB.setIndeterminate(true);
            return false;
        }
        
        //Reset
        if(PB != null) PB.setIndeterminate(true);
		return true;
    }
    
    static public void PError(String msg)
	{
		JOptionPane.showMessageDialog(null,msg,"Error",JOptionPane.ERROR_MESSAGE);
	}
    
    
    
    
    
    //NEW VM
    
    static final String CLASS_PATHM = getClassPath().trim(); //Class Path for restart
    
    static public void restart(String ClassName)
    {
    	restart(ClassName, "");
    }
    
     //Restart Program (Thanks to the makers of JAP)
    static public void restart(String ClassName,String App)
	{
		String CLASS_PATH = "";
		if(CLASS_PATHM.indexOf(';') > 0)
			CLASS_PATH = CLASS_PATHM.substring(0,CLASS_PATHM.indexOf(';')) + App + CLASS_PATHM.substring(CLASS_PATHM.indexOf(';'));
		else
			CLASS_PATH = CLASS_PATHM + App;
		// restart command
		String strRestartCommand = "";
		
		System.out.println(CLASS_PATH);

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
		strRestartCommand = pathToJava + javaExe + " \"" + CLASS_PATH + "\" " + ClassName;// + m_commandLineArgs;



	    try
		{
		    Runtime.getRuntime().exec(strRestartCommand);
			//System.out.println("Restart command: " + strRestartCommand);
		}
		catch (Exception ex)
		{
			javaExe = "java -cp"; // Linux/UNIX
			strRestartCommand = pathToJava + javaExe + " \"" + CLASS_PATH + "\" " + ClassName;// + m_commandLineArgs;

			//System.out.println("JAP restart command: " + strRestartCommand);
			try
			{
				Runtime.getRuntime().exec(strRestartCommand);
			}
			catch (Exception a_e)
			{
				System.out.println("Error auto-restart: " + ex);
			}
		}
	}
	
	//Return Class Path for update (Thanks to the makers of JAP)
	protected static String getClassPath()
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
    
    
    //HEX
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
 
	static public String toHexF(byte[] b) { return toHexF(b, b.length); }
 
	static public String toHexF(byte[] b, int len)
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
        
	static public String toHex(byte b)
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
