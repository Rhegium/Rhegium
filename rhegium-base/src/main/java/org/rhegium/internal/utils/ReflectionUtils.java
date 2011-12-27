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
package org.rhegium.internal.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.objectweb.asm.Type;
import org.rhegium.api.config.ConfigurationProvisionException;
import org.rhegium.api.typeconverter.TypeConverterManager;

public final class ReflectionUtils {

	private ReflectionUtils() {
	}

	public static final String buildClassLoaderHierachy(final ClassLoader classLoader) {
		return new StringBuilder(classLoader.getClass().getCanonicalName()).append(
				(classLoader.getParent() != null ? new StringBuilder(" => ").append(
						buildClassLoaderHierachy(classLoader.getParent())).toString() : "")).toString();
	}

	public static void injectValue(String property, Object value, Object injectable,
			TypeConverterManager typeConverterManager) {

		Class<?> propertyType = getPropertyType(value);
		Collection<Method> setters = findSetters(property, propertyType, injectable);
		if (setters == null) {
			throw new IllegalArgumentException("No legal setter for property " + property + " was found");
		}

		List<Exception> suppressedExceptions = new ArrayList<Exception>();
		for (Method setter : setters) {
			try {
				setter.setAccessible(true);
				if (propertyType == null) {
					setter.invoke(injectable);
					return;
				}
				else {
					setter.invoke(injectable, typeConverterManager.convert(value, setter.getParameterTypes()[0]));
					return;
				}
			}
			catch (Exception e) {
				// ignore and try next setter but collect for suppressed
				// exceptions (added in Java 7)
				suppressedExceptions.add(e);
			}
		}

		ConfigurationProvisionException exception = new ConfigurationProvisionException("Value for property "
				+ property + " could not be injected");

		for (Exception suppressed : suppressedExceptions) {
			exception.addSuppressed(suppressed);
		}

		throw exception;
	}

	public static Type getType(String name) {
		return Type.getType(name);
	}

	public static String buildSetter(String property) {
		return "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
	}

	private static Collection<Method> findSetters(String property, Class<?> propertyType, Object injectable) {
		Collection<Method> methods = new ArrayList<Method>();

		String setterName = buildSetter(property);
		for (Method method : injectable.getClass().getMethods()) {
			if (method.getName().equals(setterName)) {
				if (propertyType == null && method.getParameterTypes().length == 0) {
					methods.add(method);
				}
				else if (method.getParameterTypes().length == 1) {
					methods.add(method);
				}
			}
		}

		return methods;
	}

	private static Class<?> getPropertyType(Object value) {
		if (value == null) {
			return null;
		}

		if (String.class.isAssignableFrom(value.getClass())) {
			if (StringUtils.isEmpty(value.toString().trim())) {
				return null;
			}

			if ("true".equalsIgnoreCase(value.toString().trim()) || "false".equalsIgnoreCase(value.toString().trim())) {
				return boolean.class;
			}
		}

		return value.getClass();
	}

}
