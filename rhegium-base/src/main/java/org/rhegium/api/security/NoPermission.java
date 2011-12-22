package org.rhegium.api.security;

public class NoPermission extends AbstractPermission {

	public static final Permission NO_PERMISSION = new NoPermission();

	@Override
	public String getGroup() {
		return "PermissionDenied";
	}

}
