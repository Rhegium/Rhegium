package org.rhegium.internal.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bindListener(Matchers.any(), new ConfigurationTypeListener());
		bindListener(Matchers.any(), new PostConstructTypeListener());

		Multibinder<ProvisionInterceptor> multibinder = Multibinder.newSetBinder(binder(), ProvisionInterceptor.class);
		multibinder.addBinding().to(LifecycleProvisionInterceptor.class).in(Singleton.class);
	}
}
