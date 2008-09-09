package net.sf.backcomp.utils.tests;

import java.lang.reflect.Method;

import net.sf.backcomp.tests.MethodUtils;

import junit.framework.TestCase;

public class TestHex extends TestCase
{
	private final Method toHex_byte =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"toHex",
			byte.class
		);
	
	private final Method toHex_bytes =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"toHex",
			byte[].class
		);
	
	private final Method toHex_bytes_int =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"toHex",
			byte[].class,
			int.class
		);
	
	private String toHex(byte b)
	{
		return (String) MethodUtils.invokeMethod(toHex_byte, null, b);
	}
	
	private String toHex(byte[] b)
	{
		return (String) MethodUtils.invokeMethod(toHex_bytes, null, b);
	}
	
	private String toHex(byte[] b, int len)
	{
		return (String) MethodUtils.invokeMethod(toHex_bytes_int, null, b, len);
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
		byte[] b =
			new byte[]
			{
				(byte) 0xFF,
				(byte) 0xFF
			};
		assertEquals( toHex( b, 2 ) , "ffff");
	}
	
	public void test2HexBytes147A()
	{
		byte[] b = 
			new byte[]
			{
				(byte) 0x14,
				(byte) 0x7A,
				(byte) 0x00,
				(byte) 0x00
			};
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
