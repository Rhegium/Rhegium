package org.rhegium.api.serialization;

@SuppressWarnings("serial")
public class AttributeAccessorException extends RuntimeException {

	public AttributeAccessorException() {
		super();
	}

	public AttributeAccessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public AttributeAccessorException(String message) {
		super(message);
	}

	public AttributeAccessorException(Throwable cause) {
		super(cause);
	}

}
