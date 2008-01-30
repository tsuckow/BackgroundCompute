import net.sf.backcomp.debug.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class BACKPI_ResumeData
{
	long range = -1;
	long iteration = 0;
	double sum = 0;
	
	private static boolean loaded = false;
	
	private static ArrayList<BACKPI_ResumeData> queue = new ArrayList<BACKPI_ResumeData>();
	private static ArrayList<BACKPI_ResumeData> list = new ArrayList<BACKPI_ResumeData>();
	
	private static long time = new Date().getTime();
	
	static synchronized void resumeAdd(BACKPI_ResumeData dat)
	{
		list.add(dat);
	}
	
	static synchronized void resumeRemove(BACKPI_ResumeData dat)
	{
		list.remove(dat);
	}
	
	static synchronized BACKPI_ResumeData resumeNext()
	{
		if( !queue.isEmpty() )
		{
			BACKPI_ResumeData item = queue.get(0);
			queue.remove(item);
			return item;
		}
		else if(!loaded)
		{
			//Read Resume File in
			Properties res = new Properties();
			try
    		{
            	res.load( new FileInputStream( "plugins/BACKPI/BackPiResume.properties") );
    		}
			catch(Exception e)
			{
				Debug.message("Failed to load resume data.", DebugLevel.Information, e);
				return null;
			}
			
			int i = 0;
			while(true)
			{
				String answer = res.getProperty("range" + i);
				if(answer == null) break;
				
				BACKPI_ResumeData item = new BACKPI_ResumeData();
				item.range = Long.valueOf(answer);
				
				answer = res.getProperty("iteration" + i);
				if(answer == null){i++; continue;}
				item.iteration = Long.valueOf(answer);
				
				answer = res.getProperty("sum" + i);
				if(answer == null){i++; continue;}
				item.sum = Double.valueOf(answer);
				
				queue.add(item);
				
				i++;
			}
			
			loaded = true;
			
			if( !queue.isEmpty() )
			{
				BACKPI_ResumeData item = queue.get(0);
				queue.remove(item);
				return item;
			}
		}
		return null;
	}
	
	static synchronized void resumeStore()
	{
		if(new Date().getTime() - time > 30*1000)
		{
			Iterator<BACKPI_ResumeData> item = list.iterator();
			Properties res = new Properties();
			int i = 0;
			while(item.hasNext())
			{
				BACKPI_ResumeData temp = item.next();
				res.setProperty("range" + i, String.valueOf(temp.range));
				res.setProperty("iteration" + i, String.valueOf(temp.iteration));
				res.setProperty("sum" + i, String.valueOf(temp.sum));
				i++;
			}
			
			item = queue.iterator();
			
			while(item.hasNext())
			{
				BACKPI_ResumeData temp = item.next();
				res.setProperty("range" + i, String.valueOf(temp.range));
				res.setProperty("iteration" + i, String.valueOf(temp.iteration));
				res.setProperty("sum" + i, String.valueOf(temp.sum));
				i++;
			}
			
			try
			{
				res.store( new FileOutputStream( "plugins/BACKPI/BackPiResume.properties" ) , "Background Pi Resume" );
			}
			catch(Exception e)
			{
				Debug.message("Failed to store resume data.", DebugLevel.Error, e);
			}
				
			time = new Date().getTime();
		}
	}
}
