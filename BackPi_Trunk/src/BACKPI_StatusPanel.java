import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;

public class BACKPI_StatusPanel extends JPanel
{

	/**
	 * 
	 */
	private CopyOnWriteArrayList<BACKPI_Status> Threads = null;
	private JTextField tb = null;
	private JTabbedPane tPane = null;
	
	private static final long serialVersionUID = 1L;
	
	public BACKPI_StatusPanel(CopyOnWriteArrayList<BACKPI_Status> ThreadsA)
	{
		Threads = ThreadsA;
		
		tPane = new JTabbedPane();
		this.add(tPane);
		
		tb = new JTextField(20);
		this.add(tb);
		
		SThread refresh = new SThread(this);
		refresh.start();
	}
	
	class SThread extends Thread
	{
		JPanel panel = null;
		
		//int threadcount = 0;

        SThread(JPanel p)
		{
            this.panel = p;
        }   

        public void run()
        {
        	if(panel == null) return;
        	try
        	{
        		while(true)
        		{
        			Iterator<BACKPI_Status> StatusI = Threads.iterator();
        			int max = Threads.size();
        			
        			//Handle Threading Changes
        			if(max == 0)
        			{
        				//TODO: No Cores Running
        				//threadcount = 0;
        				continue;
        			}
        			if(tPane.getTabCount() != max)
        			{
        				if(max > tPane.getTabCount())
        				{
        					//TODO: Add new tab / create tabs
        					tPane.addTab("Thread " + (tPane.getTabCount()+1), new JPanel());
        				}
        				else
        				{
        					//TODO: Remove Tab / All Tabs
        					tPane.remove(tPane.getTabCount()-1);
        				}
        				//threadcount = max;
        			}
        			
        			//Update Displayed Info
					for(int i = 0; i < max && StatusI.hasNext(); ++i)
					{
        				BACKPI_Status status = StatusI.next();
        				tb.setText( String.valueOf(status.Iteration) );
					}
					
					try
		    		{
		    			Thread.sleep(100);
		    		}
		    		catch(InterruptedException e)
		    		{
		    		}
		    		
		    		if(panel.isDisplayable() == false)//No idea if this is safe or a race condition.
		    		{
		    			panel.removeAll();
		    			panel = null;
		    			Threads = null;
		    			return;
		    		}
        		}
        	}
        	catch(Exception e)
        	{
        		BC.PError( e.getMessage() );
        	}
        }
    }
}
