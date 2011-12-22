package org.rhegium.api.security;

public interface SecurityGroup {

	long getSecurityGroupId();

	String getName();

	Permission[] getPermissions();

	SecurityGroup getParent();

	boolean isPermitted(String permission, boolean defaultPermission);

	boolean hasPermission(String permission);

}
