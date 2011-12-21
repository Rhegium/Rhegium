package org.rhegium.internal.config.tokenresolver;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.rhegium.api.config.TokenResolver;
import org.rhegium.api.config.TokenResolverManager;

import com.google.inject.Inject;

class DefaultTokenResolverManager implements TokenResolverManager {

	private final Set<TokenResolver> tokenResolvers = new HashSet<TokenResolver>();
	private final ReentrantLock lock = new ReentrantLock();

	@Inject
	DefaultTokenResolverManager(Set<TokenResolver> tokenResolvers) {
		this.tokenResolvers.addAll(tokenResolvers);
	}

	@Override
	public void registerTokenResolver(final TokenResolver tokenResolver) {
		try {
			lock.lock();
			tokenResolvers.add(tokenResolver);

		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public void removeTokenResolver(final TokenResolver tokenResolver) {
		try {
			lock.lock();
			tokenResolvers.remove(tokenResolver);

		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public String resolveToken(final String token) {
		final TokenResolver[] resolvers;
		try {
			lock.lock();
			resolvers = tokenResolvers.toArray(new TokenResolver[tokenResolvers.size()]);

		}
		finally {
			lock.unlock();
		}

		for (final TokenResolver tokenResolver : resolvers) {
			final String resolved = tokenResolver.resolve(token);

			if (resolved != null) {
				return resolved;
			}
		}

		return null;
	}
}
