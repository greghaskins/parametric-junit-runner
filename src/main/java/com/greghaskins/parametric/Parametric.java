package com.greghaskins.parametric;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

public class Parametric extends Suite {

	public Parametric(final Class<?> testClass) throws InitializationError {
		super(testClass, buildRunnersForClass(testClass));
	}

	private static <T> List<Runner> buildRunnersForClass(final Class<T> testClass)
			throws InitializationError {
		final List<Method> testCasesAnnotatedMethods = findTestCasesAnnotatedMethods(testClass);

		final ArrayList<Runner> runners = new ArrayList<Runner>();
		for (final Method testCasesMethod : testCasesAnnotatedMethods) {
			runners.addAll(getTestCasesFromMethod(testCasesMethod));
		}
		return runners;
	}

	private static <T> List<Runner> getTestCasesFromMethod(final Method testCasesMethod)
			throws InitializationError {
		final ArrayList<Runner> runners = new ArrayList<Runner>();
		final Iterable<T> testCases = getTestCases(testCasesMethod);

		for (final T testCase : testCases) {
			runners.add(new ParametricRunner<T>(testCase));
		}
		return runners;
	}

	private static <T> List<Method> findTestCasesAnnotatedMethods(final Class<T> testClass)
			throws InvalidParametricTestClassException {
		final ArrayList<Method> testCasesMethods = new ArrayList<Method>();
		for (final Method method : testClass.getMethods()) {
			final TestCases annotation = method.getAnnotation(TestCases.class);
			if (annotation != null) {
				testCasesMethods.add(method);
			}
		}

		if (testCasesMethods.isEmpty()) {
			throw new InvalidParametricTestClassException(MessageFormat.format(
					"No public methods annotated with @TestCases in {0}", testClass.getName()));
		}
		return testCasesMethods;
	}

	private static <T> Iterable<T> getTestCases(final Method testCasesMethod)
			throws InitializationError {
		verifyMethodReturnsIterableOfCorrectType(testCasesMethod);
		verifyMethodDoesNotAcceptAnyArguments(testCasesMethod);
		verifyMethodIsStatic(testCasesMethod);

		final Object testCasesAsObject;
		try {
			testCasesAsObject = testCasesMethod.invoke(null);
		} catch (final Exception e) {
			throw new InitializationError(e);
		}

		return convertReturnValueToIterable(testCasesMethod, testCasesAsObject);
	}

	private static void verifyMethodReturnsIterableOfCorrectType(final Method testCasesMethod)
			throws InvalidParametricTestClassException {
		final Class<?> returnType = testCasesMethod.getReturnType();
		final Type genericReturnType = testCasesMethod.getGenericReturnType();
		if (!Iterable.class.isAssignableFrom(returnType)
				|| !(genericReturnType instanceof ParameterizedType)) {
			throw invalidReturnTypeException(testCasesMethod);
		}

		final ParameterizedType parameterizedReturnType = (ParameterizedType) genericReturnType;
		final Type[] typeArguments = parameterizedReturnType.getActualTypeArguments();

		if (!typeArguments[0].equals(testCasesMethod.getDeclaringClass())) {
			throw invalidReturnTypeException(testCasesMethod);
		}
	}

	private static InvalidParametricTestClassException invalidReturnTypeException(
			final Method testCasesMethod) {
		final Class<?> testClass = testCasesMethod.getDeclaringClass();
		return new InvalidParametricTestClassException(MessageFormat.format(
				"@TestCases {0}.{1}() does not return an Iterable<{2}>", testClass.getName(),
				testCasesMethod.getName(), testClass.getSimpleName()));
	}

	private static void verifyMethodDoesNotAcceptAnyArguments(final Method testCasesMethod)
			throws InvalidParametricTestClassException {
		if (testCasesMethod.getParameterTypes().length > 0) {
			throw new InvalidParametricTestClassException(MessageFormat.format(
					"@TestCases {0}.{1}() must not take any parameters", testCasesMethod
							.getDeclaringClass().getName(), testCasesMethod.getName()));
		}
	}

	private static void verifyMethodIsStatic(final Method testCasesMethod)
			throws InvalidParametricTestClassException {
		if (!Modifier.isStatic(testCasesMethod.getModifiers())) {
			throw new InvalidParametricTestClassException(MessageFormat.format(
					"@TestCases {0}.{1}() must be a static method", testCasesMethod
							.getDeclaringClass().getName(), testCasesMethod.getName()));
		}
	}

	private static <T> Iterable<T> convertReturnValueToIterable(final Method testCasesMethod,
			final Object returnValueAsObject) throws InvalidParametricTestClassException {
		if (returnValueAsObject == null) {
			throw new InvalidParametricTestClassException(MessageFormat.format(
					"{0}.{1}() returned a null Iterable", testCasesMethod.getDeclaringClass()
							.getName(), testCasesMethod.getName()));
		}

		@SuppressWarnings("unchecked")
		final Iterable<T> testCasesAsIterable = (Iterable<T>) returnValueAsObject;
		return testCasesAsIterable;
	}

}
