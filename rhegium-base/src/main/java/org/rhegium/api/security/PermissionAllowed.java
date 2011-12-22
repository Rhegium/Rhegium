package org.rhegium.api.security;

public class PermissionAllowed extends AbstractPermission {

	public static final Permission PERMISSION_GRANTED = new PermissionAllowed();

	@Override
	public boolean isPermittedByDefault() {
		return true;
	}

	@Override
	public String getGroup() {
		return "PermissionAllowed";
	}

}
