package com.greghaskins.parametric.examples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AdderTestWithParameterized {

	private final int left;
	private final int right;
	private final long expectedSum;

	public AdderTestWithParameterized(final int left, final int right, final long expectedSum) {
		this.left = left;
		this.right = right;
		this.expectedSum = expectedSum;
	}

	@Parameters(name = "{0} and {1} make {2}")
	public static Iterable<Object[]> getParameters() {
		final ArrayList<Object[]> cases = new ArrayList<Object[]>();
		cases.add(new Object[] { 1, 2, 3 });
		cases.add(new Object[] { 8, 46, 54 });
		cases.add(new Object[] { 42, 0, 42 });
		cases.add(new Object[] { -3, 3, 0 });
		cases.add(new Object[] { 44728, 2346, 47074 });
		cases.add(new Object[] { 2147483647, 3, 2147483650L });
		return cases;
	}

	@Test
	public void additionIsPerformedCorrectlyLeftToRight() {
		final Adder adder = new Adder();
		final long sum = adder.add(this.left, this.right);
		assertThat(sum, is(equalTo(this.expectedSum)));
	}

	@Test
	public void additionIsCommutativeRightToLeft() {
		final Adder adder = new Adder();
		final long sum = adder.add(this.right, this.left);
		assertThat(sum, is(equalTo(this.expectedSum)));
	}

}
