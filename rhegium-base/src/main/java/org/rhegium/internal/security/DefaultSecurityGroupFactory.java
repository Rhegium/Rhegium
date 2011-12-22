package org.rhegium.internal.security;

import org.rhegium.api.security.SecurityGroup;
import org.rhegium.api.security.spi.PermissionResolver;
import org.rhegium.api.security.spi.SecurityGroupFactory;
import org.rhegium.api.security.spi.SecurityGroupResolver;

import com.google.inject.Inject;

public class DefaultSecurityGroupFactory implements SecurityGroupFactory {

	@Inject
	private PermissionResolver permissionResolver;

	@Inject
	private SecurityGroupResolver securityGroupResolver;

	@Override
	public SecurityGroup create(long securityGroupId, String name, long parentSecurityGroupId) {
		return new DefaultSecurityGroup(permissionResolver, securityGroupResolver, securityGroupId, name,
				parentSecurityGroupId);
	}

}
