package org.rhegium.api.security;

import java.util.Locale;

public interface UserSession<T> {

	Principal getPrincipal();

	T getNativeSession();

	SecurityService getSecurityService();

	boolean isAutoLogin();

	boolean isLoggedIn();

	void setLocale(Locale locale);

	Locale getLocale();

}
