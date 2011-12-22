package org.rhegium.api.security;

public abstract class AbstractPermission implements Permission {

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean isPermittedByDefault() {
		return false;
	}

}
