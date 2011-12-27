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
package org.rhegium.internal.security;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.rhegium.api.security.Permission;
import org.rhegium.api.security.PermissionDeniedException;
import org.rhegium.api.security.RequiresPermission;
import org.rhegium.api.security.SecurityService;
import org.rhegium.internal.utils.PermissionsUtils;

import com.google.inject.Inject;

class SecurityInterceptor implements MethodInterceptor {

	@Inject
	private SecurityService securityService;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final Method method = invocation.getMethod();
		final RequiresPermission annotation = getAnnotation(method);

		if (annotation != null) {
			final Permission[] permissions = PermissionsUtils.getRequiredPermissions(annotation);

			if (!securityService.permissionAllowed(permissions)) {
				throw new PermissionDeniedException("Permission to method " + method.getName() + " denied");
			}
		}

		return invocation.proceed();
	}

	private RequiresPermission getAnnotation(Method method) {
		if (method.isAnnotationPresent(RequiresPermission.class)) {
			return method.getAnnotation(RequiresPermission.class);
		}

		final Class<?> clazz = method.getDeclaringClass();
		if (clazz.isAnnotationPresent(RequiresPermission.class)) {
			return clazz.getAnnotation(RequiresPermission.class);
		}

		for (Class<?> interfaze : clazz.getInterfaces()) {
			try {
				Method interfaceMethod = interfaze.getDeclaredMethod(method.getName(), method.getParameterTypes());
				if (interfaceMethod.isAnnotationPresent(RequiresPermission.class)) {
					return interfaceMethod.getAnnotation(RequiresPermission.class);
				}
			}
			catch (NoSuchMethodException e) {
				// Just ignore since interface does not seem to have that method
			}
		}

		final Class<?> superclazz = clazz.getSuperclass();
		if (superclazz != null && superclazz.equals(Object.class)) {
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

}
