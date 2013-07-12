package com.greghaskins.parametric;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.typeCompatibleWith;

import org.junit.Test;
import org.junit.runner.Runner;

public class ParametricRunnerTest {

	private static class SomeTestClass {

	}

	@Test
	public void testGetTestCase() throws Exception {
		final SomeTestClass testCase = new SomeTestClass();
		final ParametricRunner<SomeTestClass> runner = new ParametricRunner<SomeTestClass>(testCase);
		assertThat(runner.getTestCase(), equalTo(testCase));
	}

	@Test
	public void testExtendsBuiltinJUnitRunner() throws Exception {
		assertThat(ParametricRunner.class, typeCompatibleWith(Runner.class));
	}

}
