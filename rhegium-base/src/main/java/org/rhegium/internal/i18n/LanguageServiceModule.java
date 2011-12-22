package org.rhegium.internal.i18n;

import org.rhegium.api.i18n.LanguageService;

import com.google.inject.AbstractModule;

public class LanguageServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(LanguageService.class).to(DefaultLanguageService.class).asEagerSingleton();
	}

}
