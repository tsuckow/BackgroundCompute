/**
 * @(#)BACKPI_StatusPanel.java
 *
 * Background Pi ( Computes Decimal Digits of Pi )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 * @author Deathbob
 * @version 0.1 2007/2/11
 */

import net.sf.backcomp.debug.*;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.*;

import net.sf.backcomp.utils.SpringUtilities;

public class BACKPI_StatusPanel extends JPanel
{

	/**
	 * 
	 */
	private CopyOnWriteArrayList<BACKPI_Status> Threads = null;
	private JTabbedPane tPane = null;
	
	private static final long serialVersionUID = 1L;
	
	public BACKPI_StatusPanel(CopyOnWriteArrayList<BACKPI_Status> ThreadsA)
	{
		Threads = ThreadsA;
		
		tPane = new JTabbedPane();
		this.add(tPane);
		
		SThread refresh = new SThread(this);
		refresh.start();
	}
	
	class SThread extends Thread
	{
		JPanel panel = null;
		
		//int threadcount = 0;

        SThread(JPanel p)
		{
        	super("Background Pi Status Panel");
        	this.setDaemon(true);
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
        				while(max != tPane.getTabCount())
        				{
        					if(max > tPane.getTabCount())
        					{
        						//TODO: Add new tab / create tabs
        						JPanel a = new JPanel(new SpringLayout());
        						
        						a.add( new JLabel("Iteration #:", JLabel.TRAILING) );
        						JTextField tf = new JTextField(20);
        						tf.setName("Iteration");
        						a.add( tf );
        						
        						a.add( new JLabel("ItMax #:", JLabel.TRAILING) );
        						tf = new JTextField(20);
        						tf.setName("ItMax");
        						a.add( tf );
        						
        						a.add( new JLabel("Range #:", JLabel.TRAILING) );
        						tf = new JTextField(20);
        						tf.setName("Range");
        						a.add( tf );
        						
        						a.add( new JLabel("Cpu Time:", JLabel.TRAILING) );
        						tf = new JTextField(20);
        						tf.setName("CPU");
        						a.add( tf );
        						
        						a.add( new JLabel("Cpu Sleep:", JLabel.TRAILING) );
        						tf = new JTextField(20);
        						tf.setName("CPUS");
        						a.add( tf );
        						
        						a.add( new JLabel("Time Left:", JLabel.TRAILING) );
        						tf = new JTextField(20);
        						tf.setName("TLEFT");
        						a.add( tf );
        						
        						SpringUtilities.makeCompactGrid(a,
        								6, 2, //rows, cols
        								6, 6,        //initX, initY
        								6, 6);       //xPad, yPad
        						
        						tPane.addTab("Thread " + (tPane.getTabCount()+1), a);
        					}
        					else
        					{
        						//TODO: Remove Tab / All Tabs
        						tPane.remove(tPane.getTabCount()-1);
        					}
        					//threadcount = max;
        				}
        			}
        			
        			//Update Displayed Info
					for(int i = 0; i < max && StatusI.hasNext(); ++i)
					{
        				BACKPI_Status status = StatusI.next();
        				JPanel a = (JPanel)( tPane.getComponentAt(i) );
        				int j = 0;
        				String name = "";
        				
        				do{
        					name = a.getComponent(j).getName();
        					++j;
        				}while( (name==null || !name.equals("Iteration")) && j < a.getComponentCount() );
        				if(name!=null && name.equals("Iteration"))
        				{
        					( (JTextField)a.getComponent(j-1) ).setText( String.valueOf(status.Iteration) );
        				}
        				
        				do{
        					name = a.getComponent(j).getName();
        					++j;
        				}while( (name==null || !name.equals("ItMax")) && j < a.getComponentCount() );
        				if(name!=null && name.equals("ItMax"))
        				{
        					( (JTextField)a.getComponent(j-1) ).setText( String.valueOf(status.MaxIteration) );
        				}
        				
        				do{
        					name = a.getComponent(j).getName();
        					++j;
        				}while( (name==null || !name.equals("Range")) && j < a.getComponentCount() );
        				if(name!=null && name.equals("Range"))
        				{
        					( (JTextField)a.getComponent(j-1) ).setText( String.valueOf(status.Range) );
        				}
        				
        				do{
        					name = a.getComponent(j).getName();
        					++j;
        				}while( (name==null || !name.equals("CPU")) && j < a.getComponentCount() );
        				if(name!=null && name.equals("CPU"))
        				{
        					( (JTextField)a.getComponent(j-1) ).setText( String.valueOf(status.cputime) );
        				}
        				
        				do{
        					name = a.getComponent(j).getName();
        					++j;
        				}while( (name==null || !name.equals("CPUS")) && j < a.getComponentCount() );
        				if(name!=null && name.equals("CPUS"))
        				{
        					( (JTextField)a.getComponent(j-1) ).setText( String.valueOf(status.cpusleep) );
        				}
        				
        				do{
        					name = a.getComponent(j).getName();
        					++j;
        				}while( (name==null || !name.equals("TLEFT")) && j < a.getComponentCount() );
        				if(name!=null && name.equals("TLEFT"))
        				{
        					( (JTextField)a.getComponent(j-1) ).setText( String.valueOf(status.timeleft) );
        				}
					}
					
					try
		    		{
		    			Thread.sleep(100);
		    		}
		    		catch(InterruptedException e)
		    		{
		    		}
		    		
		    		JFrame jf = (JFrame)(panel.getTopLevelAncestor());
		    		jf.pack();
		    		
		    		if(jf.isDisplayable() == false)//No idea if this is safe or a race condition.
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
        		Debug.messageDlg("Exception in status dialog thread.", DebugLevel.Error, e);
        	}
        }
    }
}
