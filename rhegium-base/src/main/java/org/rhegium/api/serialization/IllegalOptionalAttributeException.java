package org.rhegium.api.serialization;

@SuppressWarnings("serial")
public class IllegalOptionalAttributeException extends RuntimeException {

	public IllegalOptionalAttributeException() {
		super();
	}

	public IllegalOptionalAttributeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalOptionalAttributeException(String message) {
		super(message);
	}

	public IllegalOptionalAttributeException(Throwable cause) {
		super(cause);
	}

}
