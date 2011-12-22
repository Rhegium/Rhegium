package org.rhegium.internal.injector;

import org.rhegium.api.lifecycle.LifecycleAware;
import org.rhegium.api.lifecycle.LifecycleManager;

import com.google.inject.Inject;
import com.google.inject.Key;

public class LifecycleProvisionInterceptor implements ProvisionInterceptor {

	@Inject
	private LifecycleManager lifecycleManager;

	@Override
	public <T> boolean accept(Key<T> key) {
		return LifecycleAware.class.isAssignableFrom(key.getTypeLiteral().getRawType());
	}

	@Override
	public <T> T intercept(Key<T> key, T value) {
		lifecycleManager.registerLifecycleAware((LifecycleAware) value);
		return value;
	}

}
