package org.rhegium.api.security.spi;

import org.rhegium.api.security.Permission;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.SecurityGroup;

public interface PermissionResolver {

	Permission[] resolvePrincipalPermissions(Principal principal);

	Permission[] resolveGroupPermissions(SecurityGroup group);

	Permission resolvePermission(Class<? extends Permission> permission);

}
