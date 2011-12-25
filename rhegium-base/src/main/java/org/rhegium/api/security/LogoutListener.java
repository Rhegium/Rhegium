package org.rhegium.api.security;

public interface LogoutListener {

	<T> void sessionInvalidated(UserSession<T> userSession);

}
