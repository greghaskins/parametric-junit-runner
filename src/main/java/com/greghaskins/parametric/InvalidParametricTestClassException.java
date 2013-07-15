package com.greghaskins.parametric;

import java.text.MessageFormat;

import org.junit.runners.model.InitializationError;

public class InvalidParametricTestClassException extends InitializationError {

	private static final long serialVersionUID = -4347207055437468177L;
	private final String message;

	public InvalidParametricTestClassException(final String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}: {1}", this.getClass().getName(), this.message);
	}

}
