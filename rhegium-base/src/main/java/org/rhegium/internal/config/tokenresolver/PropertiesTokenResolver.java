package org.rhegium.internal.config.tokenresolver;

import java.util.Properties;

import org.rhegium.api.config.TokenResolver;
import org.rhegium.internal.utils.StringUtils;

class PropertiesTokenResolver implements TokenResolver {

	@Override
	public String resolve(final String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		final Properties properties = System.getProperties();
		for (final Object key : properties.keySet()) {
			if (value.equals(key)) {
				return (String) properties.get(key);
			}
		}

		return null;
	}

}
