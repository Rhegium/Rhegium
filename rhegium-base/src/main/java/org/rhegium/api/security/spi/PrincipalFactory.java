package org.rhegium.api.security.spi;

import org.rhegium.api.security.Principal;

public interface PrincipalFactory {

	Principal create(String founder, long repoAccessId, String[] accounts);

}
