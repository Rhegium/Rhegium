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
package org.rhegium.internal.security.authenticator;

import java.util.Locale;
import java.util.Set;

import org.rhegium.api.i18n.LanguageService;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;
import org.rhegium.api.security.authenticator.AuthenticationContext;
import org.rhegium.api.security.authenticator.AuthenticationService;
import org.rhegium.api.security.authenticator.Authenticator;
import org.rhegium.api.security.authenticator.HashAuthenticationContext;
import org.rhegium.internal.security.DefaultUserSession;

import com.google.inject.Inject;

public class DefaultAuthenticationService implements AuthenticationService {

	@Inject
	private SecurityService securityService;

	@Inject
	private Set<Authenticator> authenticators;

	@Inject
	private LanguageService languageService;

	@Override
	public <T> UserSession<T> authenticate(AuthenticationContext context, T session) {
		// Build actual UserSession
		UserSession<T> userSession = buildUserSession(context, session);

		// Save UserSession into HttpSession for later use
		securityService.setUserSession(userSession);

		return userSession;
	}

	@Override
	public <T> UserSession<T> logout(T session) {
		// Build illegal UserSession to force relogin
		UserSession<T> userSession = new DefaultUserSession<T>(null, session, languageService.getDefaultLocale(), securityService);

		// Remove UserSession from ThreadLocal
		securityService.setUserSession(userSession);

		return userSession;
	}

	@Override
	public AuthenticationContext buildAuthenticationContext() {
		return new HashAuthenticationContext();
	}

	protected <T> UserSession<T> buildUserSession(AuthenticationContext context, T session) {
		final UserSession<T> userSession = findAlreadyLoggedInUser();
		if (userSession != null && userSession.getPrincipal() != null) {
			return userSession;
		}

		PrincipalResult principalResult = authenticate0(context);
		Principal principal = principalResult != null ? principalResult.getPrincipal() : null;
		boolean autoLogin = principalResult != null ? principalResult.isAutoLogin() : false;
		Locale locale = languageService.getDefaultLocale();

		return new DefaultUserSession<T>(principal, session, locale, autoLogin, securityService);
	}

	protected <T> UserSession<T> findAlreadyLoggedInUser() {
		return null;
	}

	private PrincipalResult authenticate0(AuthenticationContext context) {
		for (Authenticator authenticator : authenticators) {
			Principal principal = authenticator.authenticate(context);
			if (principal != null) {
				return new PrincipalResult(principal, authenticator.isAutoLoginAuthenticator());
			}
		}

		return null;
	}

	private class PrincipalResult {

		private final Principal principal;
		private final boolean autoLogin;

		PrincipalResult(Principal principal, boolean autoLogin) {
			this.principal = principal;
			this.autoLogin = autoLogin;
		}

		public Principal getPrincipal() {
			return principal;
		}

		public boolean isAutoLogin() {
			return autoLogin;
		}
	}

}
