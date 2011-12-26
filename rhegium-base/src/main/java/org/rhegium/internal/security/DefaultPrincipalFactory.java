package org.rhegium.internal.security;

import org.rhegium.api.security.Principal;
import org.rhegium.api.security.spi.PrincipalFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

class DefaultPrincipalFactory implements PrincipalFactory {

	@Inject
	private Injector injector;

	@Override
	public Principal create(String name, long principalId, String[] relatedNames) {
		Principal principal = new DefaultPrincipal(name, principalId, relatedNames);
		injector.injectMembers(principal);

		return principal;
	}

}
