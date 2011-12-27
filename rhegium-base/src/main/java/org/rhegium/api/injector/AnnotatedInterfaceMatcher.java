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
package org.rhegium.api.injector;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.rhegium.internal.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.matcher.AbstractMatcher;

public class AnnotatedInterfaceMatcher extends AbstractMatcher<AnnotatedElement> {

	private final Logger LOG = LoggerFactory.getLogger(AnnotatedInterfaceMatcher.class);

	private final Class<? extends Annotation> annotationType;

	public AnnotatedInterfaceMatcher(Class<? extends Annotation> annotationType) {
		this.annotationType = checkNotNull(annotationType, "annotation type");
		checkForRuntimeRetention(annotationType);
	}

	@Override
	public boolean matches(AnnotatedElement element) {
		if (element instanceof Class<?>) {
			boolean matches = matchClass((Class<?>) element);
			if (LOG.isDebugEnabled() && matches) {
				LOG.info(String.format("Class %s matched against %s", element, annotationType));
			}

			return matches;
		}

		boolean matches = getAnnotation((Method) element) != null;
		if (LOG.isDebugEnabled() && matches) {
			LOG.info(String.format("Class %s matched against %s", element, annotationType));
		}

		return matches;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof AnnotatedInterfaceMatcher
				&& ((AnnotatedInterfaceMatcher) other).annotationType.equals(annotationType);
	}

	@Override
	public int hashCode() {
		return 37 * annotationType.hashCode();
	}

	@Override
	public String toString() {
		return StringUtils.join(" ", "annotatedWith(", annotationType.getSimpleName(), ".class)");
	}

	private boolean matchClass(Class<?> clazz) {
		if (clazz.getAnnotation(annotationType) != null) {
			return true;
		}

		for (Class<?> interfaze : clazz.getInterfaces()) {
			if (interfaze.getAnnotation(annotationType) != null) {
				return true;
			}
		}

		final Class<?> superclazz = clazz.getSuperclass();
		if (superclazz != null && !superclazz.equals(Object.class)) {
			return matchClass(superclazz);
		}

		return false;
	}

	private Annotation getAnnotation(Method method) {
		if (method.isAnnotationPresent(annotationType)) {
			return method.getAnnotation(annotationType);
		}

		final Class<?> clazz = method.getDeclaringClass();
		if (clazz.isAnnotationPresent(annotationType)) {
			return clazz.getAnnotation(annotationType);
		}

		for (Class<?> interfaze : clazz.getInterfaces()) {
			try {
				Method interfaceMethod = interfaze.getDeclaredMethod(method.getName(), method.getParameterTypes());
				if (interfaceMethod.isAnnotationPresent(annotationType)) {
					return interfaceMethod.getAnnotation(annotationType);
				}
			}
			catch (NoSuchMethodException e) {
				// Just ignore since interface does not seem to have that method
			}
		}

		final Class<?> superclazz = clazz.getSuperclass();
		if (superclazz != null && !superclazz.equals(Object.class)) {
			try {
				Method superclassMethod = superclazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
				return getAnnotation(superclassMethod);
			}
			catch (NoSuchMethodException e) {
				// Just ignore since superclass does not seem to have that
				// method
			}
		}

		return null;
	}

	private void checkForRuntimeRetention(Class<? extends Annotation> annotationType) {
		Retention retention = annotationType.getAnnotation(Retention.class);
		checkArgument(retention != null && retention.value() == RetentionPolicy.RUNTIME,
				StringUtils.join(" ", "Annotation ", annotationType.getSimpleName(), " is missing RUNTIME retention"));
	}

	private void checkArgument(boolean b, String string) {
		if (!b) {
			throw new IllegalArgumentException(string);
		}
	}

	private Class<? extends Annotation> checkNotNull(Class<? extends Annotation> annotationType, String string) {
		if (annotationType == null) {
			throw new NullPointerException(StringUtils.join(" ", string, " cannot be null"));
		}

		return annotationType;
	}

}
