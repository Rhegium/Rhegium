package org.rhegium.api.config;

public interface TokenResolver {

	/**
	 * Resolves a token ${token} to a real value.
	 * 
	 * @param token
	 *            Token to resolve
	 * @return The value of the token
	 */
	String resolve(String token);

}
