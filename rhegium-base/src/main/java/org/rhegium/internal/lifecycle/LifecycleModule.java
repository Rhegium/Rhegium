package org.rhegium.internal.lifecycle;

import org.rhegium.api.lifecycle.LifecycleManager;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class LifecycleModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(LifecycleManager.class).to(DefaultLifecycleManager.class).in(Singleton.class);
	}
}
