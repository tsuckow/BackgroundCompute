

public class WorkRunner extends Thread
{	
	public void end()
	{
		
	}
	

    public void run()
    {
    	while(true)
    	{
    		String[] Plugins = Utils.getLocalPlugins();
    		for(int i = 0; i < Plugins.length; ++i)
    		{
    			Plugin plug = Utils.loadPlugin(Plugins[i]);
    			if(!plug.isRunning())
    			{
    				plug.startUpdate();
    				
    				int maxCores = 1; //Number of cores we have available
    				int pMaxCores = plug.preferredCores(maxCores); //Get how many Plugin wants
    				if(pMaxCores > maxCores) pMaxCores = maxCores; //Don't get greedy
    				
    				if(pMaxCores > 0) plug.start( pMaxCores );
    			}//~If
    			try
		    	{
		    		Thread.sleep(5000);
		    	}
		    	catch(InterruptedException e)
		    	{ 	}
    			Plugins = Utils.getLocalPlugins();//Refresh Plugin List
    			
    		}//~For
    		
    	}//~While
    	
    }//~FUNC run

}//~CLASS WorkRunner