package org.rhegium.internal.injector;

import com.google.inject.Key;

public interface ProvisionInterceptor {

	<T> boolean accept(Key<T> key);

	<T> T intercept(Key<T> key, T value);

}
