package org.rhegium.api;

public abstract class AbstractService implements Service {

	@Override
	public void initialized() {
	}

	@Override
	public void start() {
	}

	@Override
	public void shutdown() {
	}

	@Override
	public String getServiceName() {
		return getClass().getCanonicalName();
	}

}
