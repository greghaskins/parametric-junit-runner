package com.greghaskins.parametric;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

class ParametricRunner<T> extends BlockJUnit4ClassRunner {

	private final T testCase;

	ParametricRunner(final T testCase) throws InitializationError {
		super(getTestClass(testCase));
		this.testCase = testCase;
	}

	private static Class<? extends Object> getTestClass(final Object testCase)
			throws InvalidParametricTestClassException {
		if (testCase != null) {
			return testCase.getClass();
		}
		throw new InvalidParametricTestClassException("Test case instances may not be null");
	}

	T getTestCase() {
		return this.testCase;
	}

	@Override
	protected Object createTest() throws Exception {
		return this.testCase;
	}

	@Override
	protected void validateConstructor(final List<Throwable> errors) {
	}

	@Override
	protected String getName() {
		return "[" + this.testCase.toString() + "]";
	}

	@Override
	protected String testName(final FrameworkMethod method) {
		return method.getName() + getName();
	}

}
