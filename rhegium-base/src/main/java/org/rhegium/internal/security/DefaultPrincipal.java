package org.rhegium.internal.security;

import org.rhegium.api.security.Permission;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.SecurityGroup;
import org.rhegium.api.security.spi.PermissionResolver;
import org.rhegium.api.security.spi.SecurityGroupResolver;

import com.google.inject.Inject;

class DefaultPrincipal implements Principal {

	@Inject
	private PermissionResolver permissionResolver;

	@Inject
	private SecurityGroupResolver securityGroupResolver;

	private final String name;
	private final long principalId;
	private final String[] relatedNames;

	private SecurityGroup securityGroup;
	private Permission[] permissions;

	DefaultPrincipal(String name, long principalId, String[] relatedNames) {
		this.name = name;
		this.principalId = principalId;
		this.relatedNames = new String[relatedNames.length];
		System.arraycopy(relatedNames, 0, this.relatedNames, 0, relatedNames.length);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getPrincipalId() {
		return principalId;
	}

	@Override
	public String[] getRelatedNames() {
		String[] accounts = new String[this.relatedNames.length];
		System.arraycopy(this.relatedNames, 0, accounts, 0, this.relatedNames.length);
		return accounts;
	}

	@Override
	public Permission[] getPermissions() {
		if (permissions == null) {
			permissions = retrievePermissions();
		}

		return permissions;
	}

	@Override
	public SecurityGroup getSecurityGroup() {
		if (securityGroup == null) {
			securityGroup = retrieveSecurityGroup();
		}

		return securityGroup;
	}

	@Override
	public boolean isPermitted(String permission, boolean defaultPermission) {
		boolean permitted = defaultPermission;

		// First group permissions
		if (getSecurityGroup() != null) {
			permitted = getSecurityGroup().isPermitted(permission, permitted);
		}

		// Now override with local settings if special permission is existing
		return modifyByLocalPermissions(permission, permitted);
	}

	@Override
	public boolean hasPermission(String permission) {
		if (getPermissions() == null) {
			return false;
		}

		for (Permission localPermission : getPermissions()) {
			if (localPermission.getName().equals(permission)) {
				return true;
			}
		}

		return false;
	}

	private Permission[] retrievePermissions() {
		Permission[] permissions = permissionResolver.resolvePrincipalPermissions(this);
		return permissions;
	}

	private SecurityGroup retrieveSecurityGroup() {
		return securityGroupResolver.resolveSecurityGroup(this);
	}

	private boolean modifyByLocalPermissions(String permission, boolean permitted) {
		if (getPermissions() == null) {
			return permitted;
		}

		for (Permission localPermission : getPermissions()) {
			if (localPermission.getName().equals(permission)) {
				return localPermission.isPermittedByDefault();
			}
		}

		return permitted;
	}

}
