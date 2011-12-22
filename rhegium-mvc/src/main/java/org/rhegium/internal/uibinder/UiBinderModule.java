package org.rhegium.internal.uibinder;

import org.rhegium.api.uibinder.UiBinderEventService;
import org.rhegium.api.uibinder.UiBinderService;

import com.google.inject.AbstractModule;

public class UiBinderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UiBinderService.class).to(DefaultUiBinderService.class).asEagerSingleton();
		bind(UiBinderEventService.class).to(DefaultUiBinderEventService.class).asEagerSingleton();
	}

}
