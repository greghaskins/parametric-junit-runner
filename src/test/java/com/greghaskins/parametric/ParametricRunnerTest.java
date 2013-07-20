package com.greghaskins.parametric;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.typeCompatibleWith;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class ParametricRunnerTest<T> {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Ignore
	public static class SomeTestClass {

		@Test
		public void something() {
		}

	}

	@Test
	public void testGetTestCase() throws Exception {
		final SomeTestClass testCase = new SomeTestClass();
		final ParametricRunner<SomeTestClass> runner = new ParametricRunner<SomeTestClass>(testCase);
		assertThat(runner.getTestCase(), equalTo(testCase));
	}

	@Test
	public void testCreateTestReturnsPreconstructredTestCase() throws Exception {
		final SomeTestClass testCase = new SomeTestClass();
		final ClosureReference<Object> createdTest = new ClosureReference<Object>();
		new ParametricRunner<SomeTestClass>(testCase) {
			{
				createdTest.setValue(createTest());
			}
		};
		assertThat(createdTest.getValue(), sameInstance((Object) testCase));
	}

	@Test
	public void testTestClassIsSetCorrectly() throws Exception {
		final SomeTestClass testCase = new SomeTestClass();
		final ParametricRunner<SomeTestClass> runner = new ParametricRunner<SomeTestClass>(testCase);
		final Object testClass = SomeTestClass.class;
		assertThat(runner.getTestClass().getJavaClass(), equalTo(testClass));
	}

	@Test
	public void testExtendsStandardJUnitRunner() throws Exception {
		assertThat(ParametricRunner.class, typeCompatibleWith(BlockJUnit4ClassRunner.class));
	}

	@Test
	public void testNullTestCaseCausesInvalidTestClassException() throws Exception {
		this.exception.expect(Matchers
				.invalidTestClassExceptionWithMessage("Test case instances may not be null"));
		new ParametricRunner<SomeTestClass>(null);
	}

	@Ignore
	public static class SomeTestClassWithConstructor {

		@SuppressWarnings("unused")
		public SomeTestClassWithConstructor(final String arg1, final String arg2) {
		}

		@Test
		public void something() {
		}
	}

	@Test
	public void testTestClassesWithConstructorsAreNotConsideredInvalid() throws Exception {
		try {
			new ParametricRunner<SomeTestClassWithConstructor>(new SomeTestClassWithConstructor(
					null, null));
		} catch (final InitializationError error) {
			Assert.fail("Should not have thrown: " + error);
		}
	}

	@Ignore
	public static class TestClassWithToString {
		private final String toString;

		public TestClassWithToString(final String toString) {
			this.toString = toString;
		}

		@Override
		public String toString() {
			return this.toString;
		}

		@Test
		public void someTestMethod() {
			Assert.fail();
		}
	}

	@Test
	public void testNameUsesTestCaseToString() throws Exception {
		final ClosureReference<String> actualName = new ClosureReference<String>();

		new ParametricRunner<TestClassWithToString>(new TestClassWithToString("The test name")) {
			{
				actualName.setValue(getName());
			}
		};

		assertThat(actualName.getValue(), equalTo("[The test name]"));
	}

	@Test
	public void testMethodNameUsesTestCaseToString() throws Exception {
		final ClosureReference<String> actualName = new ClosureReference<String>();

		new ParametricRunner<TestClassWithToString>(new TestClassWithToString("The test name")) {
			{
				final Method testMethod = TestClassWithToString.class
						.getDeclaredMethod("someTestMethod");
				actualName.setValue(testName(new FrameworkMethod(testMethod)));
			}
		};

		assertThat(actualName.getValue(), equalTo("someTestMethod[The test name]"));

	}

	private static class ClosureReference<T> {
		private T value;

		public T getValue() {
			return this.value;
		}

		public void setValue(final T value) {
			this.value = value;
		}
	}
}
