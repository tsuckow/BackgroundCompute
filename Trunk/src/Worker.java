/**
 * @(#)Worker.java
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
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
	    		Thread.sleep(1000);
	    	}
	    	catch(InterruptedException e)
	    	{ 	}
	    	
	    	int maxthreads = Runtime.getRuntime().availableProcessors();
	    	
	    	if(maxthreads > threads.size())
	    	{
	    		WorkRunner runner = new WorkRunner();
	    		runner.setPriority(Thread.MIN_PRIORITY);
	    		System.out.println("Starting new worker thread");
	    		runner.start();
	    		threads.add(runner);
	    	}
	    	
	    	if(threads.size() > maxthreads)
	    	{
	    		threads.get(0).end();
	    		try
		    	{
		    		Thread.sleep(2000);
		    	}
		    	catch(InterruptedException e)
		    	{ 	}
	    	}
    	}
    	
    }
}