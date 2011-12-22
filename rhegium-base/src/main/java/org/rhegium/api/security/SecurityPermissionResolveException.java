package org.rhegium.api.security;

@SuppressWarnings("serial")
public class SecurityPermissionResolveException extends RuntimeException {

	public SecurityPermissionResolveException() {
		super();
	}

	public SecurityPermissionResolveException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityPermissionResolveException(String message) {
		super(message);
	}

	public SecurityPermissionResolveException(Throwable cause) {
		super(cause);
	}

}
