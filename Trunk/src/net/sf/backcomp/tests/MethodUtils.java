/**
 *
 * Background Compute ( Manages Distributed Projects )
 * Copyright (C) 2007 Thomas Suckow (Deathbob)
 *
 * @author Deathbob
 * @version 1.00 2008/09/08
 */

package net.sf.backcomp.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.Assert;

/**
 * Utilities to help with Method Reflection in JUnit Tests
 * 
 * @author Deathbob
 *
 */
public class MethodUtils
{
	/**
	 * Links into a method with the given class, name, and parameters.
	 * If sets access for private/protected members to true.
	 * 
	 * @param targetClass Class that contains the desired method.
	 * @param methodName Method name.
	 * @param argTypes Arguments for the method.
	 * @return Method
	 */
	public static Method getPMethod(
		Class<?> targetClass,
		String methodName,
		Class<?>... argTypes
	)
	{
		final Method m = getMethod( targetClass, methodName, argTypes );
		
		try
		{
			m.setAccessible( true );
		}
		catch ( SecurityException e )
		{
			e.printStackTrace();
			Assert.fail(
				"Encountered SecurityException when permissing method."
			);
			return null;
		}
		
		return m;
	}
	
	/**
	 * Links into a method with the given class, name, and parameters.
	 * 
	 * @param targetClass Class that contains the desired method.
	 * @param methodName Method name.
	 * @param argTypes Arguments for the method.
	 * @return Method
	 */
	public static Method getMethod(
		Class<?> targetClass,
		String methodName,
		Class<?>... argTypes
	)
	{
		try
		{
			return net.sf.backcomp.utils.BC.class.getDeclaredMethod(
				methodName,
				argTypes
			);
		}
		catch ( SecurityException e )
		{
			e.printStackTrace();
			Assert.fail(
				"Encountered SecurityException when getting method."
			);
			return null;
		}
		catch ( NoSuchMethodException e )
		{
			e.printStackTrace();
			Assert.fail(
				"Encountered NoSuchMethodException when getting method."
			);
			return null;
		}
	}
	
	/**
	 * Invokes the specified method object.
	 * 
	 * @param method Method to invoke
	 * @param instance Class instance to invoke on, null if static method.
	 * @param args Arguments to send.
	 * @return Result of the method.
	 */
	public static Object invokeMethod(
		Method method,
		Object instance,
		Object... args
	)
	{
		try
		{
			return method.invoke( instance, args );
		}
		catch ( IllegalArgumentException e )
		{
			e.printStackTrace();
			Assert.fail(
				"Encountered IllegalArgumentException when invoking method."
			);
			return null;
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
			Assert.fail(
				"Encountered IllegalAccessException when invoking method."
			);
			return null;
		}
		catch ( InvocationTargetException e )
		{
			e.printStackTrace();
			Assert.fail(
				"Encountered InvocationTargetException when invoking method."
			);
			return null;
		}
	}
}
