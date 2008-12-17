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

import net.sf.backcomp.plugins.PluginHandler;
import net.sf.backcomp.plugins.PluginLoader;

/**
 * This is the class for managing running Plugins.
 *  
 *
 * @author Deathbob 
 * @version 0.2.0 2008/1/11
 */

class Worker extends Thread
{
	private static boolean stop = false;
	
	Worker()
	{
		super( "Worker" );
	}
	
	@Override
	public void run()
	{
		long coreLevelMax = 0;//Max's
		long coreLevelMin = Long.MAX_VALUE;
		long coreTotal = 0;
		while ( true )
		{
			long IcoreLevelMax = 0;
			long IcoreLevelMin = Long.MAX_VALUE;
			long IcoreTotal = 0;
			
			boolean didsomething = false;
			
			final int maxthreads = Runtime.getRuntime().availableProcessors();
			if ( coreTotal < maxthreads )
			{
				System.out.println( "Idle Cores: " + ( maxthreads - coreTotal )
					+ " Min: " + coreLevelMin + " Max: " + coreLevelMax );
			}
			
			final String[] Plugins = PluginLoader.getLoadedPlugins();
			for ( int i = 0; i < Plugins.length; ++i )
			{
				final PluginHandler plug = PluginLoader.loadPlugin( Plugins[i] );
				if ( plug != null && !plug.isPaused() )
				{
					long cores = plug.getRunningCores();
					final long wanted = plug.wantedCores();
					if ( wanted < 0 )
					{
						continue; //Invalid plugin want
					}
					if ( wanted == 0 ) //Not CPU intensive
					{
						if ( !plug.isActive() )
						{
							plug.start();
						}
						continue;
					}
					
					if ( !didsomething
						&& ( coreLevelMax != coreLevelMin + 1 && coreLevelMax != coreLevelMin )
						&& cores == coreLevelMax && cores > 0 )//Max Exceeded
					{
						//TODO:Debug Logging
						System.out
							.println( "Stopped out of ballance core on plugin: "
								+ plug.getName() );
						plug.stopCore();
						cores = plug.getRunningCores();
						didsomething = true;
					}
					if ( !didsomething && cores == coreLevelMax
						&& coreTotal > maxthreads )//Too many cores
					{
						//TODO:Debug Logging
						System.out
							.println( "Stopped system exceeding core on plugin: "
								+ plug.getName() );
						plug.stopCore();
						cores = plug.getRunningCores();
						didsomething = true;
					}
					if ( !didsomething && coreTotal < maxthreads
						&& cores == coreLevelMin )
					{
						//TODO:Debug Logging
						System.out.println( "Started core on plugin: "
							+ plug.getName() );
						plug.startCore();//core started.
						didsomething = true;
					}
					
					//Update the stats
					IcoreTotal += cores;
					if ( cores < IcoreLevelMin )//Min Search
					{
						IcoreLevelMin = cores;
					}
					if ( cores > IcoreLevelMax )//Max Search
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
				Thread.sleep( 2000 );
			}
			catch ( final InterruptedException e )
			{
			}
			
			if ( stop )
			{
				break;
			}
		}
	}
	
	public static void terminate()
	{
		stop = true;
	}
}