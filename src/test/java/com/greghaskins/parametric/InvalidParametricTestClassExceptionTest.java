package com.greghaskins.parametric;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runners.model.InitializationError;

public class InvalidParametricTestClassExceptionTest {

	@Test
	public void testExtendsJUnitInitizlizationError() throws Exception {
		assertThat(InvalidParametricTestClassException.class,
				typeCompatibleWith(InitializationError.class));
	}

	@Test
	public void testThereIsOnlyOneCause() throws Exception {
		final InvalidParametricTestClassException exception = new InvalidParametricTestClassException(
				"");
		assertThat(exception.getCauses(), hasSize(1));
	}

	@Test
	public void testMessage() throws Exception {
		final String expectedMessage = "This is the message text";
		final InvalidParametricTestClassException exception = new InvalidParametricTestClassException(
				expectedMessage);
		assertThat(exception.getCauses().get(0).getMessage(), equalTo(expectedMessage));
	}

	@Test
	public void testToString() throws Exception {
		final InvalidParametricTestClassException exception = new InvalidParametricTestClassException(
				"This message is telling you what happened");
		assertThat(
				exception.toString(),
				equalTo("com.greghaskins.parametric.InvalidParametricTestClassException: This message is telling you what happened"));
	}

}
