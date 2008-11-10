import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sf.backcomp.exceptions.PluginNotPauseableError;
import net.sf.backcomp.plugins.Plugin;
import net.sf.backcomp.plugins.PluginInterconnect;

public class UnloadTest implements Plugin
{
	PluginInterconnect link;
	private volatile boolean running = false;
	
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		return "Unload Test Plugin";
	}

	@Override
	public JPanel getSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void halt()
	{
		running = false;
		if( link != null )
			link.setPluginState(PluginInterconnect.PluginState.Stopped);
	}

	@Override
	public void initialize(PluginInterconnect PI)
	{
		link = PI;
		link.setWantedCpu(0);
		link.setPluginState(PluginInterconnect.PluginState.Stopped);
		link.setPauseable(false);
	}

	@Override
	public void start()
	{
		// TODO Auto-generated method stub
		if(!running)
		{
			running = true;
			link.setPluginState(PluginInterconnect.PluginState.Running);
			myThread t = new myThread();
			t.start();
		}
	}

	@Override
	public void uninstall()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void pause()
	{
		throw new PluginNotPauseableError();
	}

	public JPanel getStatus()
	{
		return new UnloadTestStatus();
	}
	
	class myThread extends Thread
	{

		@Override
		public void run()
		{
			while(running)
			{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
			}
		}
		
	}
	
	public class UnloadTestStatus extends JPanel
	{		
			private static final long serialVersionUID = 1L;
			
			public UnloadTestStatus()
			{
				
				JButton stop = new JButton("Swap C");
		        stop.addActionListener(new StopButton());
		        this.add(stop);
			}
			
			private class StopButton implements ActionListener
			{
				
				public void actionPerformed(ActionEvent e)
		        {
		        	//Do Swap
					File thisone = new File("plugins/UnloadTest/UnloadTest$UnloadTestStatus.class");
					File otherone = new File("plugins/UnloadTest/UnloadTest$UnloadTestStatus.class.bak");
					File newthisone = new File("plugins/UnloadTest/UnloadTest$UnloadTestStatus.class.tmp");
					thisone.renameTo( newthisone );
					otherone.renameTo( new File("plugins/UnloadTest/UnloadTest$UnloadTestStatus.class") );
					newthisone.renameTo( new File("plugins/UnloadTest/UnloadTest$UnloadTestStatus.class.bak") );
					link.setPluginState(PluginInterconnect.PluginState.NeedReload);
					running = false;
					link = null;
		        }
			}
	}
}
