package org.rhegium.api.security;

public interface SecurityService {

	boolean permissionAllowed(Permission[] permissions);

	boolean hasPermission(Class<? extends Permission> permission);

	void setUserSession(UserSession<?> session);

	UserSession<?> getUserSession();

}
