

public class mainapp implements Runnable
{
	

    public void run()
    {
     	Utils.iconCreate();

		Worker WorkT = new Worker();
		WorkT.start();
    }
}