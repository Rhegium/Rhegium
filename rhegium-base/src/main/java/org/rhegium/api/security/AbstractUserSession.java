/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rhegium.api.security;

import java.util.Locale;

import org.rhegium.api.security.authenticator.Authenticator;

public abstract class AbstractUserSession<T> implements UserSession<T> {

	private final T session;
	private final Principal principal;
	private final SecurityService securityService;
	private final boolean autoLogin;

	private Locale locale;

	public AbstractUserSession(Principal principal, T session, Locale locale, SecurityService securityService) {
		this(principal, session, locale, false, securityService);
	}

	public AbstractUserSession(Principal principal, T session, Locale locale, boolean autoLogin,
			SecurityService securityService) {

		this.principal = principal;
		this.session = session;
		this.locale = locale;
		this.autoLogin = autoLogin;
		this.securityService = securityService;
	}

	@Override
	public Principal getPrincipal() {
		return principal;
	}

	@Override
	public T getNativeSession() {
		return session;
	}

	@Override
	public boolean isAutoLogin() {
		return autoLogin;
	}

	@Override
	public boolean isAuthenticated() {
		return getPrincipal() != null;
	}

	@Override
	public SecurityService getSecurityService() {
		return securityService;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public Authenticator getAuthenticator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout(LogoutListener... logoutListeners) {
		securityService.setUserSession(buildUnauthenticatedUserSession(session, locale, securityService));

		for (LogoutListener logoutListener : logoutListeners) {
			logoutListener.sessionInvalidated(this);
		}
	}

	private static <T> UserSession<T> buildUnauthenticatedUserSession(T session, Locale locale,
			SecurityService securityService) {

		return new AbstractUserSession<T>(null, session, locale, securityService) {
		};
	}

}
