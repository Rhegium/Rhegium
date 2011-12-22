package org.rhegium.api.security.authenticator;

import org.rhegium.api.security.UserSession;

public interface AuthenticationService {

	public static final String SESSION_ATTRIBUTE_SECURITY_PRINCIPAL = "RhegiumSecurityPrincipal";

	<T> UserSession<T> authenticate(AuthenticationContext context, T session);

	<T> UserSession<T> logout(T session);

	AuthenticationContext buildAuthenticationContext();

}
