package org.rhegium.internal.config.tokenresolver;

import org.rhegium.api.config.TokenResolver;
import org.rhegium.api.lifecycle.LifecycleManager;
import org.rhegium.internal.utils.StringUtils;

import com.google.inject.Inject;

class ProjectVersionTokenResolver implements TokenResolver {

	@Inject
	private LifecycleManager lifecycleManager;

	@Override
	public String resolve(final String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		if ("project.version".equals(value)) {
			return lifecycleManager.getVersion();
		}

		return null;
	}

}
