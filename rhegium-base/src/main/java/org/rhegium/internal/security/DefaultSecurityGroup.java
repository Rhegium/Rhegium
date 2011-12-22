package org.rhegium.internal.security;

import org.rhegium.api.security.Permission;
import org.rhegium.api.security.SecurityGroup;
import org.rhegium.api.security.spi.PermissionResolver;
import org.rhegium.api.security.spi.SecurityGroupResolver;

class DefaultSecurityGroup implements SecurityGroup {

	private final long securityGroupId;
	private final String name;
	private final Permission[] permissions;
	private final SecurityGroup parent;

	DefaultSecurityGroup(PermissionResolver permissionResolver, SecurityGroupResolver securityGroupResolver,
			long securityGroupId, String name, long parentSecurityGroupId) {

		this.securityGroupId = securityGroupId;
		this.name = name;

		// Resolve permissions
		this.permissions = permissionResolver.resolveGroupPermissions(this);

		if (parentSecurityGroupId != -1) {
			this.parent = securityGroupResolver.resolveSecurityGroup(parentSecurityGroupId);
		}
		else {
			this.parent = null;
		}
	}

	@Override
	public long getSecurityGroupId() {
		return securityGroupId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Permission[] getPermissions() {
		return permissions;
	}

	@Override
	public SecurityGroup getParent() {
		return parent;
	}

	@Override
	public boolean isPermitted(String permission, boolean defaultPermission) {
		if (permissions != null) {
			for (Permission localPermission : permissions) {
				if (localPermission.getName().equals(permission)) {
					return localPermission.isPermittedByDefault();
				}
			}
		}

		if (parent != null) {
			return parent.isPermitted(permission, defaultPermission);
		}

		return defaultPermission;
	}

	@Override
	public boolean hasPermission(String permission) {
		if (permissions == null) {
			return false;
		}

		for (Permission localPermission : permissions) {
			if (localPermission.getName().equals(permission)) {
				return true;
			}
		}

		return false;
	}

}
