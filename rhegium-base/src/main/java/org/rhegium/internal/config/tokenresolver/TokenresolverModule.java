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
