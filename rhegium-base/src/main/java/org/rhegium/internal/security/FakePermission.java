package org.rhegium.internal.security;

import org.rhegium.api.security.Permission;

class FakePermission implements Permission {

	private final boolean permitted;
	private final String name;

	FakePermission(String permission, boolean permitted) {
		this.name = permission;
		this.permitted = permitted;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isPermittedByDefault() {
		return permitted;
	}

	@Override
	public String getGroup() {
		return "permission";
	}

}
