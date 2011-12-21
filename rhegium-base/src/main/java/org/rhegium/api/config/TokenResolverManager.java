package org.rhegium.api.config;

public interface TokenResolverManager {

	void registerTokenResolver(TokenResolver tokenResolver);

	void removeTokenResolver(TokenResolver tokenResolver);

	String resolveToken(String token);

}
