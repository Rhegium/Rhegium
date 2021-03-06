/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rhegium.internal.security;

import org.rhegium.api.security.LoginListener;
import org.rhegium.api.security.LogoutListener;
import org.rhegium.api.security.NoPermission;
import org.rhegium.api.security.Permission;
import org.rhegium.api.security.PermissionAllowed;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;
import org.rhegium.api.security.authenticator.AuthenticationContext;
import org.rhegium.api.security.authenticator.AuthenticationService;
import org.rhegium.internal.utils.PermissionsUtils;

import com.google.inject.Inject;

class DefaultSecurityService implements SecurityService {

	private final ThreadLocal<UserSession<?>> userSession = new ThreadLocal<UserSession<?>>();

	@Inject
	private AuthenticationService authenticationService;

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
	@SuppressWarnings("unchecked")
	public <T> UserSession<T> getUserSession() {
		return (UserSession<T>) userSession.get();
	}

	@Override
	public <T> UserSession<T> login(T session, AuthenticationContext authenticationContext, LoginListener... loginListeners) {
		UserSession<T> userSession = authenticationService.authenticate(authenticationContext, session);
		for (LoginListener loginListener : loginListeners) {
			if (userSession.isAuthenticated()) {
				loginListener.loginSucceeded(userSession);
			}
			else {
				loginListener.loginFailed(userSession, authenticationContext);
			}
		}

		return userSession;
	}

	@Override
	public <T> void logout(UserSession<T> userSession, LogoutListener... logoutListeners) {
		userSession.logout(logoutListeners);
	}

}
