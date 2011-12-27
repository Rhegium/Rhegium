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

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.rhegium.api.config.ConfigurationProvisionException;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class PostConstructTypeListener implements TypeListener {

	@Override
	@SuppressWarnings("unchecked")
	public <T> void hear(TypeLiteral<T> type, TypeEncounter<T> encounter) {
		Class<T> rawtype = (Class<T>) type.getRawType();

		for (final Method method : rawtype.getDeclaredMethods()) {
			if (method.isAnnotationPresent(PostConstruct.class)) {
				if (method.getParameterTypes().length > 0) {
					throw new ConfigurationProvisionException(String.format(
							"@PostConstruct annotated method %s must not contain "
									+ "any parameters in method signature", method));
				}

				encounter.register(new InjectionListener<T>() {

					@Override
					public void afterInjection(T injectee) {
						try {
							method.invoke(injectee);
						}
						catch (Exception e) {
							throw new ProvisionException(String.format(
									"@PostConstruct annotated method %s could not be called", method), e);
						}
					}
				});
			}
		}
	}
}
