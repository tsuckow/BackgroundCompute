

public class WorkRunner extends Thread
{	
	public void end()
	{
		
	}
	

    public void run()
    {
    	boolean didsomething = true;
    	boolean coreOK = false;
    	while(true)
    	{
    		System.out.println("Check for plugins");
    		coreOK = false;
    		
    		if(!didsomething)
    		{
    			System.out.println("More cores than plugins");
    			coreOK = true;
    		}
    		
    		didsomething = false;
    		String[] Plugins = Utils.getLocalPlugins();
    		for(int i = 0; i < Plugins.length; ++i)
    		{
    			Plugin plug = Utils.loadPlugin(Plugins[i]);
    			if(!plug.isRunning())
    			{
    				System.out.println("Started new plugin: " + plug.getName());
    				plug.startUpdate();
    				plug.start();
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
    		
    	}//~While
    	
    }//~FUNC run

}//~CLASS WorkRunner