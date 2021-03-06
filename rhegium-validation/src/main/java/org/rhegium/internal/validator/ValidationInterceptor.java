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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.validator.method.MethodValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

class ValidationInterceptor implements MethodInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationInterceptor.class);

	@Inject
	private Validator validator;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final Object invoked = invocation.getThis();
		final Method method = invocation.getMethod();
		final Object[] args = invocation.getArguments();

		LOGGER.trace("Validating call: {} with args {}", method, Arrays.toString(args));

		final Set<? extends ConstraintViolation<?>> validationErrors = getMethodValidator().validateAllParameters(
				invoked, method, args);

		if (!validationErrors.isEmpty()) {
			LOGGER.trace("Invalid call - constraint problem");
			throw buildValidationException(validationErrors);
		}

		LOGGER.trace("Valid call - invoking");
		final Object value = invocation.proceed();

		final Set<? extends ConstraintViolation<?>> returnValidationErrors = getMethodValidator().validateReturnValue(
				invoked, method, value);

		if (!returnValidationErrors.isEmpty()) {
			LOGGER.trace("Invalid return value - constraint problem");
			throw buildValidationException(returnValidationErrors);
		}

		LOGGER.trace("Valid return value - returning");
		return value;
	}

	private MethodValidator getMethodValidator() {
		return validator.unwrap(MethodValidator.class);
	}

	private RuntimeException buildValidationException(final Set<? extends ConstraintViolation<?>> validationErrors) {

		final StringBuilder sb = new StringBuilder();
		for (final ConstraintViolation<?> cv : validationErrors) {
			sb.append("\n" + cv.getPropertyPath() + "{" + cv.getInvalidValue() + "} : " + cv.getMessage());
		}

		return new ValidationException(sb.toString());
	}

}
