package org.rhegium.api.security.spi;

import org.rhegium.api.security.Principal;

public interface PrincipalFactory {

	Principal create(String name, long principalId, String[] relatedNames);

}
