package org.rhegium.api.security;

import org.rhegium.api.security.authenticator.AuthenticationContext;

public interface LoginListener {

	<T> void loginFailed(UserSession<T> userSession, AuthenticationContext authenticationContext);

	<T> void loginSucceeded(UserSession<T> userSession);

}
