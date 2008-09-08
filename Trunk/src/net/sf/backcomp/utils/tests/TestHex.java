package net.sf.backcomp.utils.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public class TestHex extends TestCase
{
	private String toHex(byte b)
	{
		Method toHex;
		
		try
		{
			toHex = net.sf.backcomp.utils.BC.class.getDeclaredMethod("toHex", new Class[] {byte.class});
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		toHex.setAccessible(true);
		
		try
		{
			return (String) toHex.invoke(null, b );
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String toHex(byte[] b)
	{
		Method toHex;
		
		try
		{
			toHex = net.sf.backcomp.utils.BC.class.getDeclaredMethod("toHex", new Class[] {byte[].class});
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		toHex.setAccessible(true);
		
		try
		{
			return (String) toHex.invoke(null, b );
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String toHex(byte[] b, int len)
	{
		Method toHex;
		
		try
		{
			toHex = net.sf.backcomp.utils.BC.class.getDeclaredMethod("toHex", new Class[] {byte[].class, int.class});
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		toHex.setAccessible(true);
		
		try
		{
			return (String) toHex.invoke(null, b, len );
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void testHexByteFF()
	{
		assertEquals( toHex( (byte) 0xFF ) , "ff");
	}
	
	public void testHexByte0B()
	{
		assertEquals( toHex( (byte) 0x0B ) , "0b");
	}
	
	public void testHexByte13()
	{
		assertEquals( toHex( (byte) 0x13 ) , "13");
	}
	
	public void testHexBytes147A()
	{
		assertEquals( toHex( new byte[] {(byte) 0x14, (byte) 0x7A} ) , "147a");
	}
	
	public void testHexBytesB852()
	{
		assertEquals( toHex( new byte[] {(byte) 0xB8, (byte) 0x52} ) , "b852");
	}
	
	public void testHexBytesFFFF()
	{
		assertEquals( toHex( new byte[] {(byte) 0xFF, (byte) 0xFF}, 2 ) , "ffff");
	}
	
	public void test2HexBytes147A()
	{
		byte[] b = 
			new byte[] {(byte) 0x14, (byte) 0x7A, (byte) 0x00, (byte) 0x00};
		assertEquals( toHex( b, 2 ) , "147a");
	}
	
	public void test3HexBytesB85200()
	{
		byte[] b =
			new byte[]
			{
				(byte) 0xB8,
				(byte) 0x52,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00
			};
		assertEquals( toHex( b, 3 ) , "b85200");
	}
	
	public void test4HexBytesFFFF0000()
	{
		byte[] b =
			new byte[]
			{
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0x00,
				(byte) 0x00
			};
		assertEquals( toHex( b, 4 ) , "ffff0000");
	}
}
