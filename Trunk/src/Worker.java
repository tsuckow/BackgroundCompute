/**
 * @(#)Worker.java
 *
 *
 * @author 
 * @version 1.00 2007/2/10
 */
 
import java.util.*;

public class Worker extends Thread
{	
	ArrayList<WorkRunner> threads = new ArrayList<WorkRunner>();

    public void run()
    {
    	while(true)
    	{
    		try
	    	{
	    		Thread.sleep(2000);
	    	}
	    	catch(InterruptedException e)
	    	{ 	}
	    	
	    	int maxthreads = Runtime.getRuntime().availableProcessors();
	    	String[] Plugins = Utils.getLocalPlugins();
	    	
	    	if(maxthreads > threads.size() && Plugins.length > threads.size())
	    	{
	    		WorkRunner runner = new WorkRunner();
	    		runner.start();
	    		threads.add(runner);
	    	}
	    	
	    	if(threads.size() > maxthreads)
	    	{
	    		threads.get(0).end();
	    		try
		    	{
		    		Thread.sleep(5000);
		    	}
		    	catch(InterruptedException e)
		    	{ 	}
	    	}
    	}
    	
    }
}