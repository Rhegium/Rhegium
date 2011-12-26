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
