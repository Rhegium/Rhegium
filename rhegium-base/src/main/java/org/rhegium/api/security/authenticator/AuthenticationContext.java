package org.rhegium.api.security.authenticator;

public interface AuthenticationContext {

	<T> T getAttribute(String name);

	<T> void setAttribute(String name, T value);

}
