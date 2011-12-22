package org.rhegium.api.security;

public interface Principal {

	String getName();

	long getPrincipalId();

	String[] getRelatedNames();

	Permission[] getPermissions();

	SecurityGroup getSecurityGroup();

	boolean isPermitted(String permission, boolean defaultPermission);

	boolean hasPermission(String permission);

}
