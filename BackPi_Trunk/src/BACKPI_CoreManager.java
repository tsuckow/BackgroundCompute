import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.backcomp.plugins.PluginInterconnect;
import net.sf.backcomp.plugins.utils.CpuLimiter;


public class BACKPI_CoreManager extends Thread
{
	private final Properties Settings;
	
	private final CopyOnWriteArrayList<BACKPI_Status> Threads;
	
	private final PluginInterconnect link;
	
	volatile boolean halt = false;
	/*
	private static Properties defaultResume()
	{
		Properties set = new Properties();
		
		set.setProperty("range", "1");
		set.setProperty("iteration", "0");
		set.setProperty("sum", "0");
		
		return set;
	}
	
	private final Properties Resume = new Properties( defaultResume() );//Load settings object with defaults
	*/
	BACKPI_CoreManager(Properties Set, CopyOnWriteArrayList<BACKPI_Status> Ts, PluginInterconnect PI)
	{
		Settings = Set;
		Threads = Ts;
		link = PI;
	}
	
	@Override
	public void run()
	{
		while(!halt)
		{
			if( Threads.size() < link.getCores() )
			{
				BACKPI_Status status = new BACKPI_Status();
				Threads.add(status);
				
				BACKPI_Core core = new BACKPI_Core(Settings, status);
				status.statusCore = core;
				core.start();
			}
			
			Iterator<BACKPI_Status> i = Threads.iterator();
			
			while(i.hasNext())//Dead threads?
			{
				BACKPI_Status status = i.next();
				if( !status.statusCore.isAlive() )
				{
					Threads.remove(status);
					break;
				}
				//TODO: if more than like 20 threads have died in the last 25 seconds, something is afoot.
			}
			CpuLimiter.sleep(1000);
		}
	}
	
	public void halt()
	{
		halt = true;
	}
}
