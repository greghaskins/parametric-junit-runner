package com.greghaskins.parametric.examples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.greghaskins.parametric.Parametric;
import com.greghaskins.parametric.TestCases;

@RunWith(Parametric.class)
public class AdderTest {

	private final int left;
	private final int right;
	private final long expectedSum;

	public AdderTest(final int left, final int right, final long expectedSum) {
		this.left = left;
		this.right = right;
		this.expectedSum = expectedSum;
	}

	@TestCases
	public static Iterable<AdderTest> testCases() {
		final ArrayList<AdderTest> cases = new ArrayList<AdderTest>();
		cases.add(new AdderTest(1, 2, 3));
		cases.add(new AdderTest(8, 46, 54));
		cases.add(new AdderTest(42, 0, 42));
		cases.add(new AdderTest(-3, 3, 0));
		cases.add(new AdderTest(44728, 2346, 47074));
		cases.add(new AdderTest(2147483647, 3, 2147483650L));
		return cases;
	}

	@Override
	public String toString() {
		return MessageFormat
				.format("{0} and {1} make {2}", this.left, this.right, this.expectedSum);
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
