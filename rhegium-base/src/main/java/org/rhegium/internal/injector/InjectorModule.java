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

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;

public class InjectorModule extends AbstractModule {

	@Override
	protected void configure() {
		bindListener(Matchers.any(), new ConfigurationTypeListener());
		bindListener(Matchers.any(), new PostConstructTypeListener());

		Multibinder<ProvisionInterceptor> multibinder = Multibinder.newSetBinder(binder(), ProvisionInterceptor.class);
		multibinder.addBinding().to(LifecycleProvisionInterceptor.class).in(Singleton.class);
	}
}
