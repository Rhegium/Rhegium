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
