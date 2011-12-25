package org.rhegium.api.security;

import org.rhegium.api.security.authenticator.AuthenticationContext;

public interface SecurityService {

	boolean permissionAllowed(Permission[] permissions);

	boolean hasPermission(Class<? extends Permission> permission);

	void setUserSession(UserSession<?> session);

	<T> UserSession<T> getUserSession();

	<T> UserSession<T> login(T session, AuthenticationContext authenticationContext, LoginListener... loginListeners);

	<T> void logout(UserSession<T> userSession, LogoutListener... logoutListeners);

}
