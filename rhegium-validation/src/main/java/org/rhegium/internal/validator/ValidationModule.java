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
package org.rhegium.internal.validator;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.bootstrap.ProviderSpecificBootstrap;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.rhegium.api.injector.AnnotatedInterfaceMatcher;
import org.rhegium.api.validator.Validate;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;

public class ValidationModule extends AbstractModule {

	@Override
	protected void configure() {
		final ProviderSpecificBootstrap<HibernateValidatorConfiguration> providerSpecificBootstrap = Validation
				.byProvider(HibernateValidator.class);

		final ValidatorFactory factory = providerSpecificBootstrap.configure().buildValidatorFactory();

		// Bind validator as scope prototype
		bind(Validator.class).toProvider(new Provider<Validator>() {

			@Override
			public Validator get() {
				return factory.getValidator();
			}
		});

		ValidationInterceptor interceptor = new ValidationInterceptor();
		requestInjection(interceptor);

		// Bind interceptors for validation - first all methods annotated with
		// @Validate in any class and the second binding binds all methods in a
		// @Validate annotated class
		bindInterceptor(Matchers.any(), new AnnotatedInterfaceMatcher(Validate.class), interceptor);
		bindInterceptor(new AnnotatedInterfaceMatcher(Validate.class), Matchers.any(), interceptor);
	}

}
