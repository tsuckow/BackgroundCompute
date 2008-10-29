package net.sf.backcomp.diagnostic.plugins.UnloadTest;

import javax.swing.JPanel;

import net.sf.backcomp.Exceptions.PluginNotPauseableError;
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
}
