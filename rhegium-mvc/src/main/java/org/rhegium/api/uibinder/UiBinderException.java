package org.rhegium.api.uibinder;

@SuppressWarnings("serial")
public class UiBinderException extends RuntimeException {

	public UiBinderException() {
		super();
	}

	public UiBinderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UiBinderException(String message, Throwable cause) {
		super(message, cause);
	}

	public UiBinderException(String message) {
		super(message);
	}

	public UiBinderException(Throwable cause) {
		super(cause);
	}

}
