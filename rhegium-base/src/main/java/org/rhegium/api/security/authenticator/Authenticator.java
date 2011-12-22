package org.rhegium.api.security.authenticator;

import org.rhegium.api.security.Principal;

public interface Authenticator {

	Principal authenticate(AuthenticationContext context);

	boolean isAutoLoginAuthenticator();

}
