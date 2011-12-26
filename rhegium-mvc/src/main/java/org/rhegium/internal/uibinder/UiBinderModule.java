package org.rhegium.internal.uibinder;

import org.rhegium.api.uibinder.UiBinderEventService;

import com.google.inject.AbstractModule;

public class UiBinderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UiBinderEventService.class).to(DefaultUiBinderEventService.class).asEagerSingleton();
	}

}
