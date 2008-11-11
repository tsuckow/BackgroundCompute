
public class BACKPI_Status
{
	static enum coreMode {Initilizing, Calculating, Communicating, Stopping};
	
	public coreMode Mode = coreMode.Initilizing;
	
	public long Range = 1;
	
	public long Iteration = 0;
	public long MaxIteration = 0;
	
	public double cputime = -1;//-1, init; -2, not supported
	
	public String timeleft = "";
	
	public BACKPI_Core statusCore;
	
	public BACKPI_Status()
	{	}
}
