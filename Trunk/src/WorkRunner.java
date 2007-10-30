

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
    				plug.start();
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