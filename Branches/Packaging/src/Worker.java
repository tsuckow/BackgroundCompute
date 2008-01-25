/**
 * @(#)Worker.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 * @author Deathbob
 * @version 1.00 2007/2/10
 */

/**
 * This is the class for managing Plugins.
 *  
 *
 * @author Deathbob 
 * @version 0.2.0 2008/1/11
 */

public class Worker extends Thread
{	
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
	    	
	    	String[] Plugins = Utils.getLocalPlugins();
	    	for(int i = 0; i < Plugins.length; ++i)
	    	{
	    		Plugin plug = Utils.loadPlugin(Plugins[i]);
	    		if(plug != null)
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
    	}
    	
    }
}