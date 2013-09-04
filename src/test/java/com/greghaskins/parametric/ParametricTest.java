package com.greghaskins.parametric;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.typeCompatibleWith;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

@RunWith(Enclosed.class)
public class ParametricTest {

	public static class WhenEverythingIsSetUpCorrectly {

		@Ignore
		public static class MockParametricTestClass {

			private static Iterable<MockParametricTestClass> testCases = Collections.emptyList();

			@TestCases
			public static Iterable<MockParametricTestClass> getTestCases() {
				return testCases;
			}

			@Test
			public void someTestMethod() {
			}
		}

		@Ignore
		public static class AnotherMockParametricTestClass {

			private static Iterable<AnotherMockParametricTestClass> testCases = Collections
					.emptyList();

			@TestCases
			public static Iterable<AnotherMockParametricTestClass> testCasesMethodWithSomeOtherName() {
				return testCases;
			}

			@Test
			public void someTestMethod() {
			}
		}

		@Before
		@After
		public void reset() {
			MockParametricTestClass.testCases = Collections.emptyList();
			AnotherMockParametricTestClass.testCases = Collections.emptyList();
		}

		@Test
		public void testExtendsSuiteRunner() throws Exception {
			assertThat(Parametric.class, typeCompatibleWith(Suite.class));
		}

		@Test
		public void testSetsTestClassCorrectly() throws Exception {
			final Parametric parametric = new Parametric(MockParametricTestClass.class);
			final Object expectedJavaClass = MockParametricTestClass.class;
			assertThat(parametric.getTestClass().getJavaClass(), equalTo(expectedJavaClass));
		}

		@Test
		public void testBuildsParametricRunnerForEachTestCase() throws Exception {
			final MockParametricTestClass[] testCases = new MockParametricTestClass[] {
					new MockParametricTestClass(), new MockParametricTestClass(),
					new MockParametricTestClass() };
			MockParametricTestClass.testCases = Arrays.asList(testCases);

			final List<Runner> childRunners = constructRunnerAndGetChildren(MockParametricTestClass.class);

			assertThat(childRunners, contains(parametricRunnersForTestCases(testCases)));
		}

		@Test
		public void testUsesTestCasesAnnotationToDetermineWhichMethodGeneratesTestCases()
				throws Exception {
			final AnotherMockParametricTestClass[] testCases = new AnotherMockParametricTestClass[] {
					new AnotherMockParametricTestClass(), new AnotherMockParametricTestClass(),
					new AnotherMockParametricTestClass(), new AnotherMockParametricTestClass() };

			AnotherMockParametricTestClass.testCases = Arrays.asList(testCases);

			final List<Runner> childRunners = constructRunnerAndGetChildren(AnotherMockParametricTestClass.class);
			assertThat(childRunners, contains(parametricRunnersForTestCases(testCases)));
		}

	}

	public static class WhenThereIsNoPublicTestCasesAnnotation {

		@Rule
		public ExpectedException exception = ExpectedException.none();

		private static class SomeClassWithoutTestCasesAnnotation {

			@SuppressWarnings("unused")
			public static Iterable<SomeClassWithoutTestCasesAnnotation> someMethod() {
				return Collections.emptyList();
			}
		}

		@Test
		public void testThrowsInvalidParametricTestClassExceptionWhenNoMethodHasTestCasesMethod()
				throws Exception {
			this.exception.expect(Matchers.invalidTestClassExceptionWithMessage(MessageFormat
					.format("No public methods annotated with @TestCases in {0}",
							SomeClassWithoutTestCasesAnnotation.class.getName())));
			new Parametric(SomeClassWithoutTestCasesAnnotation.class);
		}

		private static class SomeClassWithPrivateTestCasesAnnotation {

			@TestCases
			private static Iterable<SomeClassWithPrivateTestCasesAnnotation> someMethod() {
				return Collections.emptyList();
			}
		}

		@Test
		public void testThrowsInvalidParametricTestClassExceptionWhenTestCasesMethodIsPrivate()
				throws Exception {
			this.exception.expect(Matchers.invalidTestClassExceptionWithMessage(MessageFormat
					.format("No public methods annotated with @TestCases in {0}",
							SomeClassWithPrivateTestCasesAnnotation.class.getName())));
			new Parametric(SomeClassWithPrivateTestCasesAnnotation.class);
		}
	}

	public static class WhenThereAreMultipleTestCasesMethods {

		@Ignore
		public static class TestClassWithMultipleTestCasesMethods {

			private static Iterable<TestClassWithMultipleTestCasesMethods> testCases1 = Collections
					.emptyList();
			private static Iterable<TestClassWithMultipleTestCasesMethods> testCases2 = Collections
					.emptyList();

			@TestCases
			public static Iterable<TestClassWithMultipleTestCasesMethods> testCases1() {
				return testCases1;
			}

			@TestCases
			public static Iterable<TestClassWithMultipleTestCasesMethods> testCases2() {
				return testCases2;
			}

			@Test
			public void someTest() {
			}
		}

		@Test
		public void testAggregatesCasesFromAllAnnotatedTestCasesMethods() throws Exception {
			final TestClassWithMultipleTestCasesMethods[] allCases = new TestClassWithMultipleTestCasesMethods[] {
					new TestClassWithMultipleTestCasesMethods(),
					new TestClassWithMultipleTestCasesMethods(),
					new TestClassWithMultipleTestCasesMethods(),
					new TestClassWithMultipleTestCasesMethods(),
					new TestClassWithMultipleTestCasesMethods() };
			TestClassWithMultipleTestCasesMethods.testCases1 = Arrays.asList(allCases[0],
					allCases[1], allCases[2]);
			TestClassWithMultipleTestCasesMethods.testCases2 = Arrays.asList(allCases[3],
					allCases[4]);

			final List<Runner> childRunners = constructRunnerAndGetChildren(TestClassWithMultipleTestCasesMethods.class);
			assertThat(childRunners, containsInAnyOrder(parametricRunnersForTestCases(allCases)));
		}
	}

	private static List<Runner> constructRunnerAndGetChildren(final Class<?> testClass)
			throws InitializationError {
		final List<Runner> runners = new ArrayList<Runner>();
		new Parametric(testClass) {
			{
				runners.addAll(getChildren());
			}
		};
		return runners;
	}

	private static <T> List<Matcher<? super Runner>> parametricRunnersForTestCases(
			final T[] testCases) {
		final List<Matcher<? super Runner>> runnersForTestCases = new ArrayList<Matcher<? super Runner>>();
		for (final T testCase : testCases) {
			runnersForTestCases.add(parametricRunnerBuiltWithTestCase(testCase));
		}
		return runnersForTestCases;
	}

	private static Matcher<Runner> parametricRunnerBuiltWithTestCase(final Object expectedTestCase) {

		return new TypeSafeDiagnosingMatcher<Runner>(ParametricRunner.class) {

			public void describeTo(final Description description) {
				description.appendText("ParametricRunner for ").appendValue(expectedTestCase);
			}

			@Override
			protected boolean matchesSafely(final Runner runner,
					final Description mismatchDescription) {
				final Object actualTestCase = ((ParametricRunner<?>) runner).getTestCase();
				mismatchDescription.appendText("ParametricRunner for ").appendValue(actualTestCase);
				return (actualTestCase != null) && (actualTestCase.equals(expectedTestCase));
			}

		};
	}

	public static class WhenTestCasesMethodReturnsNull {

		@Rule
		public ExpectedException exception = ExpectedException.none();

		private static class SomeClassWithoutNullListOfTestCases {

			@TestCases
			public static Iterable<SomeClassWithoutNullListOfTestCases> testCases() {
				return null;
			}
		}

		@Test
		public void testThrowsInvalidParametricTestClassExceptionWhenNoMethodHasTestCasesAnnotation()
				throws Exception {
			this.exception.expect(Matchers.invalidTestClassExceptionWithMessage(MessageFormat
					.format("{0}.testCases() returned a null Iterable",
							SomeClassWithoutNullListOfTestCases.class.getName())));
			new Parametric(SomeClassWithoutNullListOfTestCases.class);
		}
	}

	@RunWith(Parameterized.class)
	public static class WhenTestCasesMethodHasWrongReturnType {

		@Rule
		public ExpectedException exception = ExpectedException.none();
		private final Class<?> testClass;
		private final String testMethodName;

		public WhenTestCasesMethodHasWrongReturnType(final Class<?> testClass,
				final String testMethodName) {
			this.testClass = testClass;
			this.testMethodName = testMethodName;
		}

		@Test
		public void testThrowsInvalidParametricTestClassExceptionWhenTestCasesMethodReturnsWrongType()
				throws Exception {
			this.exception.expect(Matchers.invalidTestClassExceptionWithMessage(MessageFormat
					.format("@TestCases {0}.{1}() does not return an Iterable<{2}>",
							this.testClass.getName(), this.testMethodName,
							this.testClass.getSimpleName())));
			new Parametric(this.testClass);
		}

		@Parameters(name = "{1}")
		public static Collection<Object[]> getBadTestClasses() {
			final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
			parameters.add(new Object[] { TestClassWithNonIterableTestCasesReturnType.class,
					"methodWithNonIterableReturnType" });
			parameters.add(new Object[] { TestClassWithWrongIterableParameterType.class,
					"methodWithWrongParameterizedType" });
			parameters.add(new Object[] { TestClassWithRawIterableParameterType.class,
					"methodWithUnparameterizedReturnType" });
			return parameters;
		}

		private static class TestClassWithNonIterableTestCasesReturnType {

			@TestCases
			public static Object methodWithNonIterableReturnType() {
				return new Object();
			}
		}

		private static class TestClassWithWrongIterableParameterType {

			@TestCases
			public static Iterable<Object> methodWithWrongParameterizedType() {
				return Collections.emptyList();
			}
		}

		private static class TestClassWithRawIterableParameterType {

			@SuppressWarnings("rawtypes")
			@TestCases
			public static Iterable methodWithUnparameterizedReturnType() {
				return Collections.emptyList();
			}
		}

	}

	@RunWith(Parameterized.class)
	public static class WhenTestCasesMethodTakesParameters {
		@Rule
		public ExpectedException exception = ExpectedException.none();
		private final Class<?> testClass;
		private final String testMethodName;

		public WhenTestCasesMethodTakesParameters(final Class<?> testClass,
				final String testMethodName) {
			this.testClass = testClass;
			this.testMethodName = testMethodName;
		}

		@Test
		public void testThrowsInvalidParametricTestClassExceptionWhenTestCasesMethodReturnsWrongType()
				throws Exception {
			this.exception.expect(Matchers.invalidTestClassExceptionWithMessage(MessageFormat
					.format("@TestCases {0}.{1}() must not take any parameters",
							this.testClass.getName(), this.testMethodName,
							this.testClass.getSimpleName())));
			new Parametric(this.testClass);
		}

		@Parameters(name = "{1}")
		public static Collection<Object[]> getBadTestClasses() {
			final ArrayList<Object[]> parameters = new ArrayList<Object[]>();
			parameters.add(new Object[] { TestClassWithTestCasesMethodTakingOneParameter.class,
					"methodWithOneParameter" });
			parameters.add(new Object[] { TestClassWithTestCasesMethodTakingTwoParameters.class,
					"methodWithTwoParameters" });
			parameters.add(new Object[] { TestClassWithTestCasesMethodTakingVarargsParameter.class,
					"methodWithVarargsParameter" });
			return parameters;
		}

		private static class TestClassWithTestCasesMethodTakingOneParameter {

			@TestCases
			public static Iterable<TestClassWithTestCasesMethodTakingOneParameter> methodWithOneParameter(
					@SuppressWarnings("unused") final Object something) {
				return Collections.emptyList();
			}
		}

		private static class TestClassWithTestCasesMethodTakingTwoParameters {

			@TestCases
			public static Iterable<TestClassWithTestCasesMethodTakingTwoParameters> methodWithTwoParameters(
					@SuppressWarnings("unused") final Object thingOne,
					@SuppressWarnings("unused") final Object thingTwo) {
				return Collections.emptyList();
			}
		}

		private static class TestClassWithTestCasesMethodTakingVarargsParameter {

			@TestCases
			public static Iterable<TestClassWithTestCasesMethodTakingVarargsParameter> methodWithVarargsParameter(
					@SuppressWarnings("unused") final Object... things) {
				return Collections.emptyList();
			}
		}

	}

	public static class WhenAnExceptionOccursWhileInvokingTestCasesMethod {

		@Rule
		public ExpectedException expectedException = ExpectedException.none();

		private static class TestClassWithTestCasesMethodThatThrowsUnexpectedException {

			@TestCases
			public static Iterable<TestClassWithTestCasesMethodThatThrowsUnexpectedException> testCases() {
				throw new RuntimeException();
			}

		}

		@Test
		public void testThrowsInitializationErrorWrappingUnexpectedException() throws Exception {
			this.expectedException.expect(InitializationError.class);
			new Parametric(TestClassWithTestCasesMethodThatThrowsUnexpectedException.class);
		}

	}

	public static class WhenTestCasesMethodIsNotStatic {

		@Rule
		public ExpectedException exception = ExpectedException.none();

		@Test
		public void testThrowsRuntimeExceptionWhenUnableToInvokeTestCasesMethod() throws Exception {
			this.exception.expect(Matchers.invalidTestClassExceptionWithMessage(MessageFormat
					.format("@TestCases {0}.testCases() must be a static method",
							TestClassWithNonStaticTestCasesMethod.class.getName())));
			new Parametric(TestClassWithNonStaticTestCasesMethod.class);
		}

		private static class TestClassWithNonStaticTestCasesMethod {

			@TestCases
			public Iterable<TestClassWithNonStaticTestCasesMethod> testCases() {
				return Collections.emptyList();
			}

		}

	}

}
