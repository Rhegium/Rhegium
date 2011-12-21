package org.rhegium.api.lifecycle;

public interface LifecycleAware {

	void initialized();

	void start();

	void shutdown();

}
