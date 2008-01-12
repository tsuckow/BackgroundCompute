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
    	
    	while(true)
    	{
    		long IcoreLevelMax = 0;
        	long IcoreLevelMin = 0;
        	long coreTotal = 0;
    		boolean didsomething = false;
    		
	    	int maxthreads = Runtime.getRuntime().availableProcessors();
	    	
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
	    				plug.startCore();
	    				cores = plug.getRunningCores();
	    				didsomething = true;
	    			}
	    			
	    			//Update the stats
	    			coreTotal += cores;
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
/*	    	
	    	if(!didsomething && coreCount < maxthreads)
	    	{
	    		coreLevel++;
	    	}
	    		
	    	didsomething = false;
	    	
	    	
	    	
	    	String[] Plugins = Utils.getLocalPlugins();
	    	for(int i = 0; i < Plugins.length; ++i)
	    	{
	    		Plugin plug = Utils.loadPlugin(Plugins[i]);
	    		if(plug.getState() == Plugin.state.Stopped)
	    		{
	    				System.out.println("Started new plugin: " + plug.getName());
	    				plug.startCore();
	    				didsomething = true;
	    			}//~If
	    			else if(!didsomething && coreOK)
	    			{
	    				System.out.println("Started new core: " + plug.getName());
	    				plug.core();
	    				didsomething = true;
	    			}
	    				
	    			try
			    	{
			    		Thread.sleep(3000);//TODO: Lower this number.
			    	}
			    	catch(InterruptedException e)
			    	{ 	}
	    			Plugins = Utils.getLocalPlugins();//Refresh Plugin List
	    			
	    		}//~For
*/
	    	
	    	try
	    	{
	    		Thread.sleep(2000);
	    	}
	    	catch(InterruptedException e)
	    	{ 	}
    	}
    	
    }
}