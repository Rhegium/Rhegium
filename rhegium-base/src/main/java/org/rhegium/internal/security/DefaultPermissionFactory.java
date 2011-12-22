package org.rhegium.internal.security;

import org.rhegium.api.security.Permission;
import org.rhegium.api.security.spi.PermissionFactory;

public class DefaultPermissionFactory implements PermissionFactory {

	@Override
	public Permission create(String permission, boolean permitted) {
		return new FakePermission(permission, permitted);
	}

}
