/**
 * @(#)BACKPI_Plugin.java
 *
 * Background Pi ( Computes Decimal Digits of Pi )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * support@defcon1.hopto.org
 *
 * @author Deathbob
 * @version 0.1 2006/12/20
 */

import net.sf.backcomp.plugins.Plugin;
import net.sf.backcomp.utils.*;
import net.sf.backcomp.debug.*;

import java.awt.TrayIcon;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.*;

public class BACKPI_Plugin extends Plugin
{
	
	private CopyOnWriteArrayList<BACKPI_Status> Threads = new CopyOnWriteArrayList<BACKPI_Status>();
	private static Properties defaultSettings()
	{
		Properties set = new Properties();
		
		//set.setProperty("username", "");
		set.setProperty("server_path", "http://defcon1.hopto.org/backpi/");
		
		return set;
	}
	private static Properties defaultResume()
	{
		Properties set = new Properties();
		
		set.setProperty("range", "1");
		set.setProperty("iteration", "0");
		set.setProperty("sum", "0");
		
		return set;
	}
	public final static Properties Settings = new Properties( defaultSettings() );//Load settings object with defaults
	public final static Properties Resume = new Properties( defaultResume() );//Load settings object with defaults
	
	public BACKPI_Plugin() //{}
	{}
	
    //int i = 0;
    
	
	private long comm_nextRange()
	{
        
		try
		{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse ("http://defcon1.hopto.org/backpi/getNextRange.php" + ( (Settings.getProperty("username") == null)?"":"?username=" + Settings.getProperty("username")) );

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            
            //Get the result tag
            if( doc.getDocumentElement().getNodeName().equals("result") )
            {
            	//Check for server errors.
            	NodeList Errors = doc.getElementsByTagName("error");
            	if(Errors.getLength() > 0)
            	{
            		for(int j=0; j<Errors.getLength() ; j++)
            		{
            			Node Error = Errors.item(j);
            			System.out.println( Error.getTextContent() );
            		}
            	}
            	else
            	{
            		//TODO: When no error take what we need.
            		NodeList Ranges = doc.getElementsByTagName("range");
                	//System.out.println( Ranges.getLength() );
                	if(Ranges.getLength() == 1)
                	{
                		Node Range = Ranges.item(0);
                		//System.out.println( Range.getNodeValue() );
                		System.out.println( Range.getTextContent() );
                		return Integer.parseInt(Range.getTextContent());
                		
                	}
                	else
                	{
                		//FIXME: no range.
                	}
            	}
            }
            else
            {
            	//TODO: Invalid Result From Server
            }


            

        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable th) {
        th.printStackTrace ();
        }
        
		return 1000;
	}
	
	/**
	 * 
	 * Communicates with the server and submits a range
	 * 
	 * @param range
	 * 	Computation Range
	 * @param data
	 * 	9 digits
	 */
	private void comm_submitRange(long range, int data)
	{
		if(range == 1 && data != 141592653)
		{
			Debug.messageDlg("Computation Error Detected In Plugin BACKPI!", DebugLevel.Fatal);
			stopAll(true,true);
			throw new Error("BACKPI: Calculation of range 1 failed.");
		}
		
		try
		{
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse ("http://defcon1.hopto.org/backpi/submitRange.php?range=" + range + "&data=" + data + ( (Settings.getProperty("username") == null)?"":"&username=" + Settings.getProperty("username")));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            
            //Get the result tag
            if( doc.getDocumentElement().getNodeName().equals("result") )
            {
            	//Check for server errors.
            	NodeList Errors = doc.getElementsByTagName("error");
            	if(Errors.getLength() > 0)
            	{
            		for(int j=0; j<Errors.getLength() ; j++)
            		{
            			Node Error = Errors.item(j);
            			System.out.println( Error.getTextContent() );
            		}
            	}
            	else
            	{
            		//FIXME: We don't care
            	}
            }
            else
            {
            	//TODO: Invalid Result From Server
            }


            

        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable th) {
        th.printStackTrace ();
        }
	}
	
    private static long mul_mod(long a, long b, long m)//Long in java is a c++ Long Long
    {
    	return (a * b) % m;
    }
    
    /**
     *
     * @param q
     * @param n
     * @return Modular Inverse of ( q mod n )
     */
    /*private static long inv_mod(long q, long n) // http://www.mersennewiki.org/index.php/Modular_arithmetic#Modular_inversion
    {
    	   long i = q, h = n, v = 0, d = 1;
    	   
    	   do
    	   {
    		   long t = i / h;
    		   long x = h;
    		   h = i - t * x;
    		   i = x;
    		   x = d;
    		   d = v - t * x;
    		   v = x;
    	   }while( h > 0 );
    	  return v % q;
    }*/
    /* return the inverse of x mod y */
    long inv_mod(long x,long y) {
      long q,u,v,a,c,t;

      u=x;
      v=y;
      c=1;
      a=0;
      do {
        q=v/u;
        
        t=c;
        c=a-q*c;
        a=t;
        
        t=u;
        u=v-q*u;
        v=t;
      } while (u!=0);
      a=a%y;
      if (a<0) a=y+a;
      return a;
    }

    /* return the inverse of u mod v, if v is odd */
    /*int inv_mod2(int u,int v) {
      int u1,u3,v1,v3,t1,t3;
      
      u1=1;
      u3=u;
      
      v1=v;
      v3=v;
      
      if ((u&1)!=0) {
        t1=0;
        t3=-v;
        goto Y4;
      } else {
        t1=1;
        t3=u;
      }
      
      do {
        
        do {
          if ((t1&1)==0) {
    	t1=t1>>1;
    	t3=t3>>1;
          } else {
    	t1=(t1+v)>>1;
    	t3=t3>>1;
          }
          Y4:
        t3=t3;
        } while ((t3&1)==0);
        
        if (t3>=0) {
          u1=t1;
          u3=t3;
        } else {
          v1=v-t1;
          v3=-t3;
        }
        t1=u1-v1;
        t3=u3-v3;
        if (t1<0) {
          t1=t1+v;
        }
      } while (t3 != 0);
      return u1;
    }*/
    
    /* return (a^b) mod m */
    private static long pow_mod(long a,long b,long m)
    {
      long r,aa;
       
      r=1;
      aa=a;
      while (true) {
        if( (b & 1) != 0 ) r=mul_mod(r,aa,m);
        b=b>>1;
        if (b == 0) break;
        aa=mul_mod(aa,aa,m);
      }
      return r;
    }
    
   	private static boolean isPrime(long n) // http://snippets.dzone.com/posts/show/2879
   	{
		boolean prime = true;
		for (long i = 3; i <= Math.sqrt(n); i += 2)
			if (n % i == 0) {
				prime = false;
				break;
			}
		if (( n%2 !=0 && prime && n > 2) || n == 2) {
			return true;
		} else {
			return false;
		}
	}
   	
   	/** return the prime number immediatly after n */
   	private static long next_prime(long n)
   	{
   	   do {
   	      n++;
   	   } while (!isPrime(n));
   	   return n;
   	}
   	
   	private long PrimeCount(double bound)
   	{
   	    return (long)Math.floor( ( bound )/( Math.log( bound )-1.083) );
   	}
   	
   	private boolean coreShutdown(BACKPI_Status status)
   	{
   		if( currentCoreShouldExit() )
   		{
   			Threads.remove(status);
   			return true;
   		}
   		return false;
   	}
   	
   	private static String formatTime(long timeRemaining)
   	{
   		String result = "";
   		int sec = (int)(timeRemaining/1000) % 60;
   		int min = (int)(timeRemaining/(1000*60)) % 60;
   		int hours = (int)(timeRemaining/(1000*60*60)) % 24;
   		int days = (int)(timeRemaining/(1000*60*60*24)) % 7;
   		int weeks = (int)(timeRemaining/(1000*60*60*24*7));
   		if (weeks != 0)
   			result = weeks + " weeks ";
   		if (days != 0 || weeks != 0)
   			result += days + " days ";
   		result += ((hours<10)?"0":"") + hours + ":" + ((min<10)?"0":"") + min + ":" + ((sec<10)?"0":"") + sec;
   		return result;
   	}
   	
   	@Override
    public void main()
    {
   		BACKPI_Status status = new BACKPI_Status();
   		Threads.add(status);
   		
    	long n = 1;
    	
    	while(true)
    	{
    		BACKPI_ResumeData rdata = null;
    		
    		//
    		//
    		//
    		//
    		status.Mode = BACKPI_Status.coreMode.Initilizing;
    		
    		long av,a,vmax,N,num,den,k,kq1,kq2,kq3,kq4,t,v,s,i,t1;
    		double sum;
    		
    		a=2;
    		
    		rdata = BACKPI_ResumeData.resumeNext();
    		if(rdata != null)
    		{
    			n = rdata.range;
    			a = next_prime(rdata.iteration);
    			sum = rdata.sum;
    			
    			Tray.iconMessage("Iteration: " + a,"Resumed: " + n,TrayIcon.MessageType.INFO);
    		}
    		else
    		{
    			n = comm_nextRange();
            	sum = 0;
            	a=2;
            	
            	rdata = new BACKPI_ResumeData();
            	rdata.range = n;
            	rdata.iteration = a;
            	rdata.sum = sum;
    		}
    		
    		if(n <= 0 || a < 2 || sum < 0)
    		{
    			Debug.message("BACKPI: Invalid Calculation Setup\n\n" + n + "\n" + a + "\n" + sum, DebugLevel.Error);
    			continue;
    		}
    		
    		BACKPI_ResumeData.resumeAdd(rdata);
    		
    		N=(int)((n+20)*Math.log(10)/Math.log(13.5));
    		
    		status.MaxIteration = PrimeCount(3*N);
  			status.Range = n;
  			
    		status.Mode = BACKPI_Status.coreMode.Calculating;
    		
    		final int iTimeNum = 200;
    		int iTimePos = 0;
    		long[] iTime = new long[iTimeNum];

    		
    		long lTime = new Date().getTime();//Time for 1 loop
    		
    		for(/*a=2*/;a<=(3*N);a=next_prime(a))
    		{
    			iTime[iTimePos] = new Date().getTime()-lTime;
    			lTime = new Date().getTime();
    			iTimePos = (iTimePos + 1) % iTimeNum;
    				
    			if(coreShutdown(status)) return;
    			
    			{
    				status.Iteration = PrimeCount(a);
    						
    				status.cputime = getCpuUsage();
    			
    				long timetotal = 0;
    				for(short j = 0; j < iTimeNum; ++j){timetotal += iTime[j];}
    				
    				long timeleft = ( timetotal*( PrimeCount(3*N)-PrimeCount(a) ) /  ( iTimeNum ) ) ;
    				
    				status.timeleft = formatTime(timeleft);
    			
    			}
    					
    			if(coreShutdown(status)) return;
    			
    		    vmax=(int)(Math.log(3*N)/Math.log(a));
    		    if (a==2) {
    		    	vmax=vmax+(N-n);
    		    	if (vmax<=0) continue;
    		    }
    		    av=1;
    		    for(i=0;i<vmax;i++) av=av*a;

    		    s=0;
    		    den=1;
    		    kq1=0;
    		    kq2=-1;
    		    kq3=-3;
    		    kq4=-2;
    		    if (a==2) {
    		    	num=1;
    		    	v=-n; 
    		    } else {
    		    	num=pow_mod(2,n,av);
    		    	v=0;
    		    }

    		    //These are used in the loop
    		    long vinc = 0;
		    	long kq = 0;
		    	long kqinc = 0;
    		    for(k=1;k<=N;k++)
    		    {

    		    	t=2*k;
    		      
    		    	//DIVN DIVN(t,a,v,vinc,kq,kqinc)
    		    	vinc = -1;
    		    	kq = kq1;
    		    	kqinc = 2;

    		    	kq+=kqinc;
    		    	if (kq >= a)
    		    	{
    		    		do
    		    		{
    		    			kq-=a;
    		    		} while(kq>=a);
    		    		if (kq == 0)
    		    		{
    		    			do
    		    			{
    		    				t=t/a;
    		    				v+=vinc;
    		    			} while ((t % a) == 0);
    		    		}
    		    	}
    		    	
    		    	kq1 = kq;
    		    	//DIVN(t,a,v,-1,kq1,2);
    		    	
    		    	num=mul_mod(num,t,av);
    		    	t=2*k-1;
    		    	
    		    	//DIVN DIVN(t,a,v,vinc,kq,kqinc)
    		    	vinc = -1;
    		    	kq = kq2;
    		    	kqinc = 2;
    		    	
    		    	kq+=kqinc;
    		    	if (kq >= a)
    		    	{
    		    		do
    		    		{
    		    			kq-=a;
    		    		} while(kq>=a);
    		    		if (kq == 0)
    		    		{
    		    			do
    		    			{
    		    				t/=a;
    		    				v+=vinc;
    		    			} while((t % a) == 0);
    		    		}
    		    	}
    		    	
    		    	kq2 = kq;
    		    	//DIVN(t,a,v,-1,kq2,2);
    		    	
    		    	num=mul_mod(num,t,av);
    		    	t=3*(3*k-1);
    		    	
    		    	//DIVN DIVN(t,a,v,vinc,kq,kqinc)
    		    	vinc = 1;
    		      	kq = kq3;
    		      	kqinc = 9;
    		      	
    		      	kq+=kqinc;
    		      	if (kq >= a)
    		      	{
    		      		do
    		      		{
    		      			kq-=a;
    		      		} while(kq>=a);
    		      		if (kq == 0)
    		      		{
    		      			do
    		      			{
    		      				t/=a;
    		      				v+=vinc;
    		      			} while((t % a) == 0);
    		      		}
    		      	}
    		      	
    		      	kq3 = kq;
    		      	//DIVN(t,a,v,1,kq3,9);
    		      	
    		      	den=mul_mod(den,t,av);
    		      	t=(3*k-2);
    		      	
    		      	//DIVN DIVN(t,a,v,vinc,kq,kqinc)
    		      	vinc = 1;
    		      	kq = kq4;
    		      	kqinc = 3;
    		    	  
    		      	kq+=kqinc;
    		      	if (kq >= a)
    		      	{
    		      		do
    		      		{
    		      			kq-=a;
    		      		} while(kq>=a);
    		      		if (kq == 0)
    		    	  	{
    		      			do
    		      			{
    		      				t/=a;
    		      				v+=vinc;
    		      			} while ((t % a) == 0);
    		    	  	}
    		      	}
    		    	  
    		      	kq4 = kq;
    		      	//DIVN(t,a,v,1,kq4,3);
    		      	
    		      	if (a!=2)
    		      		t=t*2;
    		      	else
    		      		v++;
    		      
    		      	den=mul_mod(den,t,av);
    		      
    		      	if (v > 0)
    		      	{
    		      		if (a!=2)
    		      			t=inv_mod(den,av);
    		      		else
    		      			t=inv_mod(den,av);
    		    	  
    		      		t=mul_mod(t,num,av);
    		      		for(i=v;i<vmax;i++)
    		      			t=mul_mod(t,a,av);
    		      		t1=(25*k-3);
    		      		t=mul_mod(t,t1,av);
    		      		s+=t;
    		      		if (s>=av)
    		      			s-=av;
    		      	}
    		    }

    		    t=pow_mod(5,n-1,av);
    		    s=mul_mod(s,t,av);
    		    sum=((sum+(double) s/ (double) av) % 1.0);
    		    
    		    rdata.range = n;
    		    rdata.iteration = a;
    		    rdata.sum = sum;
    		    BACKPI_ResumeData.resumeStore();
    		  }
    		  
    		  status.Mode = BACKPI_Status.coreMode.Communicating;    		  
    		  
    		  int fsum = new Double(sum * 1000000000).intValue();
    		  
    		  Tray.iconMessage("BP: " + fsum,"Message: " + n,TrayIcon.MessageType.INFO);
    		  
    		//
    		//
    		//
    		//
    		
    		comm_submitRange(n,fsum);
    		BACKPI_ResumeData.resumeRemove(rdata);
            
            
            
    		  
    		//n+=9;
    		
    		//Utils.iconMessage("GRUB!","Message: " + i,TrayIcon.MessageType.INFO);
    		try
    		{
    			Thread.sleep(5000);
    		}
    		catch(InterruptedException e)
    		{
    		}/*
    		try
    		{
    			Thread.sleep(1);
    		}
    		catch(InterruptedException e)
    		{
    		}
    		*/
    		if(coreShutdown(status)) return;
    	}
    }
    
   	@Override
    public String getName()
    {
    	return "Background Pi";
    }
    
    @Override
    public String getInfo()
    {
    	return "<img src='" + BACKPI_Plugin.class.getResource("images/Info.png") + "'>";
    }
    
    @Override
    public JPanel getSettings()
    {
    	return null;
    }
    
    @Override
    public JPanel getStatus()
    {
    	JPanel statusPanel = new BACKPI_StatusPanel(Threads);
    	return statusPanel;
    }
    
    @Override
    public boolean needCore()
    {
    	return true;
    }
    
    @Override
    protected void core()
    {
    	main();
    }
    
    public void remove()
    {
    	//BC.PError("Removing...");
    	//TODO: Add uninstall code (should basicly just delete my own directory)
    }
}

/*NodeList listOfPersons = doc.getElementsByTagName("person");
int totalPersons = listOfPersons.getLength();
System.out.println("Total no of people : " + totalPersons);

for(int s=0; s<listOfPersons.getLength() ; s++){


    Node firstPersonNode = listOfPersons.item(s);
    if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){


        Element firstPersonElement = (Element)firstPersonNode;

        //-------
        NodeList firstNameList = firstPersonElement.getElementsByTagName("first");
        Element firstNameElement = (Element)firstNameList.item(0);

        NodeList textFNList = firstNameElement.getChildNodes();
        System.out.println("First Name : " + 
               ((Node)textFNList.item(0)).getNodeValue().trim());

        //-------
        NodeList lastNameList = firstPersonElement.getElementsByTagName("last");
        Element lastNameElement = (Element)lastNameList.item(0);

        NodeList textLNList = lastNameElement.getChildNodes();
        System.out.println("Last Name : " + 
               ((Node)textLNList.item(0)).getNodeValue().trim());

        //----
        NodeList ageList = firstPersonElement.getElementsByTagName("age");
        Element ageElement = (Element)ageList.item(0);

        NodeList textAgeList = ageElement.getChildNodes();
        System.out.println("Age : " + 
               ((Node)textAgeList.item(0)).getNodeValue().trim());

        //------


    }//end of if clause


}//end of for loop with s var
*/