package com.greghaskins.parametric;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import org.junit.Test;

public class TestCasesTest {

	@Test
	public void testIsRetainedAtRuntime() throws Exception {
		final Retention retention = TestCases.class.getAnnotation(Retention.class);
		assertThat(retention.value(), equalTo(RetentionPolicy.RUNTIME));
	}

	@Test
	public void testTargetsMethodsOnly() throws Exception {
		final Target target = TestCases.class.getAnnotation(Target.class);
		assertThat(Arrays.asList(target.value()), contains(ElementType.METHOD));
	}

}
