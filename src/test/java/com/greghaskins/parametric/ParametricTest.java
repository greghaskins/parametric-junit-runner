package com.greghaskins.parametric;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.typeCompatibleWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import com.greghaskins.parametric.Annotations.TestCases;

public class ParametricTest {

	private static class MockParametricTestClass {

		private static Iterable<MockParametricTestClass> testCases = Collections.emptyList();

		@TestCases
		public static Iterable<MockParametricTestClass> getTestCases() {
			return testCases;
		}

		public static void setTestCases(final Iterable<MockParametricTestClass> testCases) {
			MockParametricTestClass.testCases = testCases;
		}
	}

	@Before
	@After
	public void reset() {
		MockParametricTestClass.testCases = Collections.emptyList();
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
		MockParametricTestClass.setTestCases(Arrays.asList(testCases));

		final List<Runner> runners = buildRunnerAndGetChildren(MockParametricTestClass.class);

		assertThat(runners, hasSize(3));
		assertThat(runners.get(0), builtWithTestCase(testCases[0]));
		assertThat(runners.get(1), builtWithTestCase(testCases[1]));
		assertThat(runners.get(2), builtWithTestCase(testCases[2]));
	}

	private List<Runner> buildRunnerAndGetChildren(final Class<?> testClass)
			throws InitializationError {
		final List<Runner> runners = new ArrayList<Runner>();
		new Parametric(testClass) {
			{
				runners.addAll(getChildren());
			}
		};
		return runners;
	}

	private Matcher<Runner> builtWithTestCase(final Object expectedTestCase) {

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
}
