package org.rhegium.api.security.spi;

import org.rhegium.api.security.Permission;

public interface PermissionFactory {

	Permission create(String permission, boolean permitted);

}
