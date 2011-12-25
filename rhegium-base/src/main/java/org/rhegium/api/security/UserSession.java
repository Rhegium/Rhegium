package org.rhegium.api.security;

import java.util.Locale;

import org.rhegium.api.security.authenticator.Authenticator;

public interface UserSession<T> {

	Principal getPrincipal();

	T getNativeSession();

	SecurityService getSecurityService();

	boolean isAutoLogin();

	boolean isAuthenticated();

	void setLocale(Locale locale);

	Locale getLocale();

	void logout(LogoutListener... logoutListeners);

	Authenticator getAuthenticator();

}
