package org.rhegium.internal.config.tokenresolver;

import java.io.File;
import java.io.IOException;

import org.rhegium.api.config.TokenResolver;
import org.rhegium.internal.utils.StringUtils;

class AppPathTokenResolver implements TokenResolver {

	private final String path;

	public AppPathTokenResolver() throws IOException {
		final File file = new File(".");

		final String temp = file.getAbsolutePath().replace("\\", "/").replace("/.", "");

		path = (!temp.startsWith("/") ? "/" : "") + temp;
	}

	@Override
	public String resolve(final String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		if ("app.path".equals(value)) {
			return path;
		}

		return null;
	}

}
