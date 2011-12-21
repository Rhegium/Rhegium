package org.rhegium.internal.config.tokenresolver;

import java.util.Map;

import org.rhegium.api.config.TokenResolver;
import org.rhegium.internal.utils.StringUtils;

class EnvTokenResolver implements TokenResolver {

	@Override
	public String resolve(final String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		final Map<String, String> environment = System.getenv();
		for (final String key : environment.keySet()) {
			if (value.equals(key)) {
				return environment.get(key);
			}
		}

		return null;
	}

}
