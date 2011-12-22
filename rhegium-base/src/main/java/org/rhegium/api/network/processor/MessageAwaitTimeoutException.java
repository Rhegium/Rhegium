package org.rhegium.api.network.processor;

@SuppressWarnings("serial")
public class MessageAwaitTimeoutException extends RuntimeException {

	public MessageAwaitTimeoutException() {
		super();
	}

	public MessageAwaitTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageAwaitTimeoutException(String message) {
		super(message);
	}

	public MessageAwaitTimeoutException(Throwable cause) {
		super(cause);
	}

}
