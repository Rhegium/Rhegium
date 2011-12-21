package org.rhegium.internal.injector;

import org.rhegium.api.lifecycle.LifecycleAware;
import org.rhegium.internal.utils.LifecycleUtils;

import com.google.inject.Key;

public class LifecycleInitializedProvisionInterceptor implements ProvisionInterceptor {

	@Override
	public <T> boolean accept(Key<T> key) {
		return LifecycleAware.class.isAssignableFrom(key.getTypeLiteral().getRawType());
	}

	@Override
	public <T> T intercept(Key<T> key, T value) {
		LifecycleUtils.startLifecycleEntity(value);
		return value;
	}

}
