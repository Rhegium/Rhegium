package org.rhegium.internal.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		Multibinder<ProvisionInterceptor> multibinder = Multibinder.newSetBinder(binder(), ProvisionInterceptor.class);
		multibinder.addBinding().to(LifecycleInitializedProvisionInterceptor.class).in(Singleton.class);
	}
}
