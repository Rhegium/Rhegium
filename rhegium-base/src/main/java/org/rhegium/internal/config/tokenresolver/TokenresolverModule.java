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
package org.rhegium.internal.config.tokenresolver;

import org.rhegium.api.config.TokenResolver;
import org.rhegium.api.config.TokenResolverManager;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class TokenresolverModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TokenResolverManager.class).to(DefaultTokenResolverManager.class).in(Singleton.class);

		Multibinder<TokenResolver> multibinder = Multibinder.newSetBinder(binder(), TokenResolver.class);
		multibinder.addBinding().to(AppPathTokenResolver.class).in(Singleton.class);
		multibinder.addBinding().to(EnvTokenResolver.class).in(Singleton.class);
		multibinder.addBinding().to(ProjectVersionTokenResolver.class).in(Singleton.class);
		multibinder.addBinding().to(PropertiesTokenResolver.class).in(Singleton.class);
	}

}
