package org.rhegium.internal.utils;

import java.util.HashMap;
import java.util.Map;

import org.rhegium.api.security.Permission;
import org.rhegium.api.security.RequiresPermission;

public class PermissionsUtils {

	private static final Map<Class<? extends Permission>, Permission> PERMISSIONS_CACHE = new HashMap<Class<? extends Permission>, Permission>();

	private PermissionsUtils() {
	}

	public static Permission[] getRequiredPermissions(RequiresPermission permission) {
		if (permission.values().length > 0) {
			final Permission[] permissions = new Permission[permission.values().length];

			for (int i = 0; i < permission.values().length; i++) {
				final Class<? extends Permission> explicitPermission = permission.values()[i];
				permissions[i] = getPermissionInstance(explicitPermission);
			}

			return permissions;
		}

		return new Permission[] { getPermissionInstance(permission.value()) };
	}

	public static Permission getPermissionInstance(Class<? extends Permission> clazz) {
		Permission permission = PERMISSIONS_CACHE.get(clazz);
		if (permission == null) {
			try {
				permission = clazz.newInstance();
				PERMISSIONS_CACHE.put(clazz, permission);
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

		return permission;
	}

}
