package com.greghaskins.parametric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

public class Parametric extends Suite {

	public Parametric(final Class<?> testClass) throws InitializationError {
		super(testClass, buildRunnersForClass(testClass));
	}

	private static <T> List<Runner> buildRunnersForClass(final Class<T> testClass) {

		final Method testCasesMethod;
		try {
			testCasesMethod = testClass.getMethod("getTestCases");
			final Iterable<T> testCases = (Iterable<T>) testCasesMethod.invoke(null);

			final ArrayList<Runner> runners = new ArrayList<Runner>();
			for (final T testCase : testCases) {

				runners.add(new ParametricRunner<T>(testCase));
			}
			return runners;
		} catch (final NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
