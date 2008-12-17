package net.sf.backcomp.utils.tests.launcher;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.sf.backcomp.tests.MethodUtils;

public class TestHex extends TestCase
{
	private final Method toHex_byte =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"toHex",
			byte.class );
	
	private final Method toHex_bytes =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"toHex",
			byte[].class );
	
	private final Method toHex_bytes_int =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"toHex",
			byte[].class,
			int.class );
	
	private String toHex( final byte b )
	{
		return ( String ) MethodUtils.invokeMethod( toHex_byte, null, b );
	}
	
	private String toHex( final byte[] b )
	{
		return ( String ) MethodUtils.invokeMethod( toHex_bytes, null, b );
	}
	
	private String toHex( final byte[] b, final int len )
	{
		return ( String ) MethodUtils.invokeMethod(
			toHex_bytes_int,
			null,
			b,
			len );
	}
	
	public void testHexByteFF()
	{
		assertEquals( "ff", toHex( ( byte ) 0xFF ) );
	}
	
	public void testHexByte0B()
	{
		assertEquals( "0b", toHex( ( byte ) 0x0B ) );
	}
	
	public void testHexByte13()
	{
		assertEquals( "13", toHex( ( byte ) 0x13 ) );
	}
	
	public void testHexBytes147A()
	{
		assertEquals(
			"147a",
			toHex( new byte[] { ( byte ) 0x14, ( byte ) 0x7A } ) );
	}
	
	public void testHexBytesB852()
	{
		assertEquals(
			"b852",
			toHex( new byte[] { ( byte ) 0xB8, ( byte ) 0x52 } ) );
	}
	
	public void testHexBytesFFFF()
	{
		final byte[] b = new byte[] { ( byte ) 0xFF, ( byte ) 0xFF };
		assertEquals( "ffff", toHex( b, 2 ) );
	}
	
	public void test2HexBytes147A()
	{
		final byte[] b =
			new byte[] { ( byte ) 0x14, ( byte ) 0x7A, ( byte ) 0x00,
					( byte ) 0x00 };
		assertEquals( "147a", toHex( b, 2 ) );
	}
	
	public void test3HexBytesB85200()
	{
		final byte[] b =
			new byte[] { ( byte ) 0xB8, ( byte ) 0x52, ( byte ) 0x00,
					( byte ) 0x00, ( byte ) 0x00 };
		assertEquals( "b85200", toHex( b, 3 ) );
	}
	
	public void test4HexBytesFFFF0000()
	{
		final byte[] b =
			new byte[] { ( byte ) 0xFF, ( byte ) 0xFF, ( byte ) 0x00,
					( byte ) 0x00 };
		assertEquals( "ffff0000", toHex( b, 4 ) );
	}
}
