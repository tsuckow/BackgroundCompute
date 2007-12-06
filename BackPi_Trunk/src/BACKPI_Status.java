
public class BACKPI_Status
{
	static enum coreMode {Initilizing, Calculating, Communicating, Stopping};
	
	public coreMode Mode = coreMode.Initilizing;
	
	public int Percent = 0;
	
	public long Range = 1;
	
	public long Iteration = 0;
	public long MaxIteration = 0;
	
	public BACKPI_Status()
	{
		//Init
	}
}
