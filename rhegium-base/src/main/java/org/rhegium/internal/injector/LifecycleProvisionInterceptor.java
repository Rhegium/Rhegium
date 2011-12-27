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
package org.rhegium.internal.injector;

import org.rhegium.api.lifecycle.LifecycleAware;
import org.rhegium.api.lifecycle.LifecycleManager;

import com.google.inject.Inject;
import com.google.inject.Key;

public class LifecycleProvisionInterceptor implements ProvisionInterceptor {

	@Inject
	private LifecycleManager lifecycleManager;

	@Override
	public <T> boolean accept(Key<T> key) {
		return LifecycleAware.class.isAssignableFrom(key.getTypeLiteral().getRawType());
	}

	@Override
	public <T> T intercept(Key<T> key, T value) {
		lifecycleManager.registerLifecycleAware((LifecycleAware) value);
		return value;
	}

}
