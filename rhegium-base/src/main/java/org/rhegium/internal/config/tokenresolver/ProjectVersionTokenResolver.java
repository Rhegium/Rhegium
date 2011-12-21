package org.rhegium.internal.config.tokenresolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.rhegium.api.config.TokenResolver;
import org.rhegium.internal.utils.StringUtils;

class ProjectVersionTokenResolver implements TokenResolver {

	private final String version;

	public ProjectVersionTokenResolver() throws IOException {
		final InputStream is = ProjectVersionTokenResolver.class.getClassLoader().getResourceAsStream(
				"META-INF/version.properties");

		if (is != null) {
			final Properties properties = new Properties();
			properties.load(is);
			version = properties.getProperty("com.yujinserver.version").replace("-SNAPSHOT", ".SNAPSHOT");
		}
		else {
			version = "unknown";
		}
	}

	@Override
	public String resolve(final String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		if ("project.version".equals(value)) {
			return version;
		}

		return null;
	}

}
