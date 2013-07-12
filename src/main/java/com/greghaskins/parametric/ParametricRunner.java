package com.greghaskins.parametric;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

class ParametricRunner<T> extends Runner {

	private final T testCase;

	ParametricRunner(final T testCase) {
		this.testCase = testCase;
	}

	T getTestCase() {
		return this.testCase;
	}

	@Override
	public Description getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(final RunNotifier notifier) {
		// TODO Auto-generated method stub
	}

}
