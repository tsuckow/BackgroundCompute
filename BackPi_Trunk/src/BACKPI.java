/**
 * @(#)BACKPI_Plugin.java
 *
 * Background Pi ( Computes Decimal Digits of Pi )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
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
 * @author Deathbob
 * @version 0.1 2006/12/20
 */


/*
 * Calculatations should be loaded from a file, one calc per line.
 * Keep them in a buffer. As cores are added, it take one as a work unit.
 * If more needed, get another from server. If not enough cores, then hang on to them.
 */

import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;

import net.sf.backcomp.exceptions.NotImplementedError;
import net.sf.backcomp.exceptions.PluginNotPauseableError;
import net.sf.backcomp.plugins.Plugin;
import net.sf.backcomp.plugins.PluginInterconnect;

public class BACKPI implements Plugin
{
	PluginInterconnect link;
	private volatile boolean running = false;
	
	private final CopyOnWriteArrayList<BACKPI_Status> Threads = new CopyOnWriteArrayList<BACKPI_Status>();
	
	private volatile BACKPI_CoreManager manager;
	
	private static Properties defaultSettings()
	{
		Properties set = new Properties();
		
		//set.setProperty("username", "");
		set.setProperty("server_path", "http://defcon1.hopto.org/backpi/");
		
		return set;
	}
	
	private final Properties Settings = new Properties( defaultSettings() );//Load settings object with defaults
    
   	@Override
    public String getName()
    {
    	return "Background Pi";
    }
    
    @Override
    public String getInfo()
    {
    	return "<img src='" + this.getClass().getResource("images/Info.png") + "'>";
    }
    
    @Override
    public JPanel getSettings()
    {
    	return null;
    }
    
    @Override
    public JPanel getStatus()
    {
    	JPanel statusPanel = new BACKPI_StatusPanel(Threads);
    	return statusPanel;
    }
    
	@Override
	public void halt()
	{
		running = false;
		if( link != null )
			link.setPluginState(PluginInterconnect.PluginState.Stopped);
		if( manager != null )
		{
			manager.halt();
			manager = null;
		}
			
	}
	
	@Override
	public void initialize(PluginInterconnect PI) {
		link = PI;
		link.setWantedCpu(2);//TODO: Eventually this should be large number
		link.setPluginState(PluginInterconnect.PluginState.Stopped);
		link.setPauseable(false);
	}
	
	@Override
	public void pause()
	{
		throw new PluginNotPauseableError();
	}
	
	@Override
	public void start()
	{
		if(!running)
		{
			running = true;
			link.setPluginState(PluginInterconnect.PluginState.Running);
			manager = new BACKPI_CoreManager( Settings, Threads, link );
			manager.start();
		}
	}
	
	@Override
	public void uninstall()
	{
		//TODO: Add uninstall code (should basically just delete my own directory)
		throw new NotImplementedError();
	}
}