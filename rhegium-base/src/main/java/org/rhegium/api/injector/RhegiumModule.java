/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rhegium.api.injector;

import org.rhegium.api.security.authenticator.AuthenticationService;
import org.rhegium.api.security.authenticator.Authenticator;
import org.rhegium.internal.config.ConfigurationModule;
import org.rhegium.internal.config.tokenresolver.TokenresolverModule;
import org.rhegium.internal.i18n.LanguageModule;
import org.rhegium.internal.injector.InjectorModule;
import org.rhegium.internal.lifecycle.LifecycleModule;
import org.rhegium.internal.modules.ModulesModule;
import org.rhegium.internal.security.SecurityModule;
import org.rhegium.internal.security.SecurityResolverModule;
import org.rhegium.internal.serialization.SerializationModule;
import org.rhegium.internal.serialization.accessor.AccessorModule;
import org.rhegium.internal.typeconverter.TypeconverterModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;

public abstract class RhegiumModule extends AbstractModule {

	private Multibinder<Authenticator> authenticators;

	@Override
	protected final void configure() {
		// Basic services
		installBaseServices();

		// Prepare multibinders
		authenticators = Multibinder.newSetBinder(binder(), Authenticator.class);

		// Additional configuration
		configureRhegium();
	}

	protected abstract void configureRhegium();

	private void installBaseServices() {
		install(new LifecycleModule());
		install(new ModulesModule());
		install(new SerializationModule());
		install(new AccessorModule());
		install(new InjectorModule());
	}

	protected void bindAuthenticationService(Class<? extends AuthenticationService> authenticationService) {
		bind(AuthenticationService.class).to(authenticationService).in(Singleton.class);
	}

	protected void bindAuthenticationService(Provider<AuthenticationService> authenticationService) {
		bind(AuthenticationService.class).toProvider(authenticationService).in(Singleton.class);
	}

	protected void bindAuthenticationService(AuthenticationService authenticationService) {
		bind(AuthenticationService.class).toProvider(Providers.of(authenticationService)).in(Singleton.class);
	}

	protected void addAuthenticator(Class<? extends Authenticator> authenticator) {
		authenticators.addBinding().to(authenticator).in(Singleton.class);
	}

	protected void addAuthenticator(Provider<Authenticator> authenticator) {
		authenticators.addBinding().toProvider(authenticator).in(Singleton.class);
	}

	protected void addAuthenticator(Authenticator authenticator) {
		authenticators.addBinding().toProvider(Providers.of(authenticator)).in(Singleton.class);
	}

	protected void installSecurityService(boolean includingResolvers) {
		install(new SecurityModule());

		if (includingResolvers) {
			install(new SecurityResolverModule());
		}
	}

	protected void installConfigurationService() {
		install(new ConfigurationModule());
		install(new TypeconverterModule());
		install(new TokenresolverModule());
	}

	protected void installLanguageService() {
		install(new LanguageModule());
	}

}
