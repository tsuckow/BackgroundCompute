/**
 * @(#)Worker.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 * @author Deathbob
 * @version 1.00 2007/2/10
 */

package net.sf.backcomp.utils;

import net.sf.backcomp.plugins.Plugin;
import net.sf.backcomp.plugins.PluginLoader;


/**
 * This is the class for managing Plugins.
 *  
 *
 * @author Deathbob 
 * @version 0.2.0 2008/1/11
 */

class Worker extends Thread
{
	private static boolean stop = false;
	
    public void run()
    { 	
    	long coreLevelMax = 0;//Max's
    	long coreLevelMin = Long.MAX_VALUE;
    	long coreTotal = 0;
    	while(true)
    	{
    		long IcoreLevelMax = 0;
        	long IcoreLevelMin = Long.MAX_VALUE;
        	long IcoreTotal = 0;
        	
    		boolean didsomething = false;
    		
	    	int maxthreads = Runtime.getRuntime().availableProcessors();
	    	if(coreTotal < maxthreads) System.out.println("Idle Cores: " + (maxthreads - coreTotal) + " Min: " + coreLevelMin + " Max: " + coreLevelMax);
	    	
	    	String[] Plugins = PluginLoader.getLocalPlugins();
	    	for(int i = 0; i < Plugins.length; ++i)
	    	{
	    		Plugin plug = PluginLoader.loadPlugin(Plugins[i]);
	    		if(plug != null && plug.getState() != Plugin.PluginState.Paused)
	    		{
	    			long cores = plug.getRunningCores();
	    			
	    			if(!didsomething && (coreLevelMax != coreLevelMin+1 && coreLevelMax != coreLevelMin) && cores == coreLevelMax && cores > 0)//Max Exceeded
	    			{
	    				System.out.println("Stopped out of ballance core on plugin: " + plug.getName());
	    				plug.stopCore(false);
	    				cores = plug.getRunningCores();
	    				didsomething = true;
	    			}
	    			if(!didsomething && cores == coreLevelMax && coreTotal > maxthreads)//Too many cores
	    			{
	    				System.out.println("Stopped system exceeding core on plugin: " + plug.getName());
	    				plug.stopCore(true);
	    				cores = plug.getRunningCores();
	    				didsomething = true;
	    			}
	    			if(!didsomething && coreTotal < maxthreads && cores == coreLevelMin)
	    			{
	    				System.out.println("Started core on plugin: " + plug.getName());
	    				if ( plug.startCore() )//core started.
	    				{
	    					cores = plug.getRunningCores();
	    					didsomething = true;
	    				}
	    			}
	    			
	    			//Update the stats
	    			IcoreTotal += cores;
	    			if(cores < IcoreLevelMin)//Min Search
	    			{
	    				IcoreLevelMin = cores;
	    			}
	    			if(cores > IcoreLevelMax)//Max Search
	    			{
	    				IcoreLevelMax = cores;
	    			}
	    		}
	    	}
    	
	    	coreLevelMax = IcoreLevelMax;
	    	coreLevelMin = IcoreLevelMin;
	    	coreTotal = IcoreTotal;
	    	
	    	try
	    	{
	    		Thread.sleep(2000);
	    	}
	    	catch(InterruptedException e)
	    	{ 	}
	    	
	    	if(stop) break;
    	} 	
    }
    
    public static void terminate()
    {
    	stop = true;
    }
}