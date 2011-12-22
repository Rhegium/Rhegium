package org.rhegium.internal.security;

import org.rhegium.api.security.AbstractPermission;

public class DeveloperViewPermission extends AbstractPermission {

	@Override
	public String getGroup() {
		return "DeveloperOnly";
	}

}
