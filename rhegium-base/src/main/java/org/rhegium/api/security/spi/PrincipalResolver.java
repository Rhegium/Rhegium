package org.rhegium.api.security.spi;

import org.rhegium.api.security.Principal;

public interface PrincipalResolver {

	Principal resolvePrincipal(long principalId);

}
