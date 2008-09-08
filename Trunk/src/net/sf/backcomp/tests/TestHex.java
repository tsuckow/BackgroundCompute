package net.sf.backcomp.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public class TestHex extends TestCase
{
	public void testHexByteFF()
	{
			Method toHex;
			
			try
			{
				toHex = net.sf.backcomp.utils.BC.class.getDeclaredMethod("toHex", new Class[] {byte.class});
				toHex.setAccessible(true);
				String result = (String) toHex.invoke(null, (byte) 0xFF );
				
				assertEquals(result, "ff");
			}
			catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
	}
	
	public void testHexByte0B()
	{
			Method toHex;
			
			try
			{
				toHex = net.sf.backcomp.utils.BC.class.getDeclaredMethod("toHex", new Class[] {byte.class});
				toHex.setAccessible(true);
				String result = (String) toHex.invoke(null, (byte) 0x0B );
				
				assertEquals(result, "0b");
			}
			catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
	}
	
	public void testHexByte13()
	{
			Method toHex;
			
			try
			{
				toHex = net.sf.backcomp.utils.BC.class.getDeclaredMethod("toHex", new Class[] {byte.class});
				toHex.setAccessible(true);
				String result = (String) toHex.invoke(null, (byte) 0x13 );
				
				assertEquals(result, "13");
			}
			catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}
	}
}
