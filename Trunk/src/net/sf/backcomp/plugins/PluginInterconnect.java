package net.sf.backcomp.plugins;

//FIXME: Synchronization, this class is interacted with by many threads.

public class PluginInterconnect
{
	/**
	 * The number of CPU cores wanted by the Plugin.<br>
	 * A value of 0 means the plugin uses a negligible amount of CPU.<br>
	 * A value of < 0 is not allowed.
	 */
	private int CpuResources = 1;
	private boolean IsPauseable = false;
	private PluginState State = PluginState.Initilizing;
		
	/**
	 * The run state the plugin is in.
	 */
	public enum PluginState
	{
		Initilizing, Running, Paused, Stopped, NeedReload, Removed
	};
	
	/**
	 * Sets the number of CPU cores wanted by the Plugin.<br>
	 * A value of 0 means the plugin uses a negligible amount of CPU.<br>
	 * A value of < 0 is not allowed.
	 * 
	 * @param num Number of CPU cores wanted.
	 */
	public void setWantedCpu(int num)
	{
		if(num >= 0)
			CpuResources = num;
	}
	
	/**
	 * Returns the number of CPU cores requested.
	 * @return Number of CPU cores wanted.
	 */
	public int getWantedCpu()
	{
		return CpuResources;
	}
	
	public void setPluginState(PluginState ps)
	{
		State = ps;
	}
	
	public PluginState getPluginState()
	{
		return State;
	}
	
	public void setPauseable(boolean pauseable)
	{
		IsPauseable = pauseable;
	}
	
	public boolean isPauseable()
	{
		return IsPauseable;
	}
}
