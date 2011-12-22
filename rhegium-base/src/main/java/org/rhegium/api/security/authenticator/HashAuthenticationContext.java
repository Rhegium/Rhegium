package org.rhegium.api.security.authenticator;

import java.util.HashMap;
import java.util.Map;

public class HashAuthenticationContext implements AuthenticationContext {

	private final Map<String, Object> attributes = new HashMap<String, Object>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T) attributes.get(name);
	}

	@Override
	public <T> void setAttribute(String name, T value) {
		attributes.put(name, value);
	}

}
