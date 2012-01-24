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
