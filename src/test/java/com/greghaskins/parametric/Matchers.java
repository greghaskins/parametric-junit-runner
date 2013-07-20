package com.greghaskins.parametric;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.runners.model.InitializationError;

public class Matchers {

	public static Matcher<? extends InitializationError> invalidTestClassExceptionWithMessage(
			final String message) {
		return new TypeSafeDiagnosingMatcher<InitializationError>(
				InvalidParametricTestClassException.class) {
	
			public void describeTo(final Description description) {
				description.appendValue(new InvalidParametricTestClassException(message));
			}
	
			@Override
			protected boolean matchesSafely(final InitializationError item,
					final Description mismatchDescription) {
				final InvalidParametricTestClassException exception = (InvalidParametricTestClassException) item;
				mismatchDescription.appendText(exception.toString());
				return message.equals(item.getCauses().get(0).getMessage());
			}
		};
	
	}

}
