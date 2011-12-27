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
