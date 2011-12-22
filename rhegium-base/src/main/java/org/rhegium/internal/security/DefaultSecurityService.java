package org.rhegium.internal.security;

import org.rhegium.api.security.NoPermission;
import org.rhegium.api.security.Permission;
import org.rhegium.api.security.PermissionAllowed;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;
import org.rhegium.internal.utils.PermissionsUtils;

class DefaultSecurityService implements SecurityService {

	private final ThreadLocal<UserSession<?>> userSession = new ThreadLocal<UserSession<?>>();

	@Override
	public boolean permissionAllowed(Permission[] permissions) {
		UserSession<?> session = getUserSession();

		if (session == null || session.getPrincipal() == null) {
			return false;
		}

		Principal principal = session.getPrincipal();

		// If permission is always granted we do not need further checking
		if (permissions.length == 1 && permissions[0].equals(PermissionAllowed.PERMISSION_GRANTED)) {
			return true;
		}

		// If no login is available we try to see if all requested permissions
		// default to permitted, so we grant access to method
		if (principal == null) {
			for (Permission permission : permissions) {
				// If permission if blocked for every request just return false
				if (permission.equals(NoPermission.NO_PERMISSION)) {
					return false;
				}

				// At least one permission isn't granted by default, we can stop
				// searching at this point
				if (!permission.isPermittedByDefault()) {
					return false;
				}
			}

			// At this point all permissions seem to grant access
			return true;
		}

		// Now we check users with available login
		for (Permission permission : permissions) {
			if (!principal.isPermitted(permission.getName(), permission.isPermittedByDefault())) {
				return false;
			}
		}

		// Here we're fine again to grant access
		return true;
	}

	@Override
	public boolean hasPermission(Class<? extends Permission> permission) {
		UserSession<?> session = getUserSession();

		if (session == null || session.getPrincipal() == null) {
			return false;
		}

		Permission temp = PermissionsUtils.getPermissionInstance(permission);
		return permissionAllowed(new Permission[] { temp });
	}

	@Override
	public void setUserSession(UserSession<?> session) {
		userSession.set(session);
	}

	@Override
	public UserSession<?> getUserSession() {
		return userSession.get();
	}

}
