package net.sf.backcomp.utils.tests.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import junit.framework.TestCase;
import net.sf.backcomp.tests.MethodUtils;

public class TestLocalFile extends TestCase
{
	private final Method getLocalHash_String =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"getLocalHash",
			String.class );
	
	private final Method getLocalList_String =
		MethodUtils.getPMethod(
			net.sf.backcomp.utils.BC.class,
			"getLocalList",
			String.class );
	
	private String getLocalHash( final String file )
	{
		return ( String ) MethodUtils.invokeMethod(
			getLocalHash_String,
			null,
			file );
	}
	
	private String[] getLocalList( final String file )
	{
		return ( String[] ) MethodUtils.invokeMethod(
			getLocalList_String,
			null,
			file );
	}
	
	public void testHash4Line()
	{
		assertEquals(
			"e604c4e9d3ab472bb7c019a0c497157b",
			getLocalHash( "TestFiles" + File.separator + "FourLines.txt" ) );
	}
	
	public void testHash4Line_EndBlank()
	{
		assertEquals(
			"651eb684635062e68097331b0dd1fc19",
			getLocalHash( "TestFiles" + File.separator
				+ "FourLines_EndBlank.txt" ) );
	}
	
	public void testHash4Line_MidBlank()
	{
		assertEquals(
			"92e0d80fdcefd00fa5db5ae5c51e6a14",
			getLocalHash( "TestFiles" + File.separator
				+ "FourLines_MidBlank.txt" ) );
	}
	
	public void testHashMissing()
	{
		assertEquals( "", getLocalHash( "FileDoesntExist.nope" ) );
	}
	
	public void testList4Line()
	{
		assertTrue( Arrays.equals( new String[] { "Line 1", "Line 2", "Line 3",
				"Line 4" }, getLocalList( "TestFiles" + File.separator
			+ "FourLines.txt" ) ) );
	}
	
	public void testList4Line_EndBlank()
	{
		assertTrue( Arrays.equals( new String[] { "Line 1", "Line 2", "Line 3",
				"Line 4" }, getLocalList( "TestFiles" + File.separator
			+ "FourLines_EndBlank.txt" ) ) );
	}
	
	public void testList4Line_MidBlank()
	{
		assertTrue( Arrays.equals( new String[] { "Line 1", "Line 2", "",
				"Line 3", "Line 4" }, getLocalList( "TestFiles"
			+ File.separator + "FourLines_MidBlank.txt" ) ) );
	}
	
	public void testListMissing()
	{
		assertTrue( Arrays.equals(
			new String[] {},
			getLocalList( "FileDoesntExist.nope" ) ) );
	}
}
