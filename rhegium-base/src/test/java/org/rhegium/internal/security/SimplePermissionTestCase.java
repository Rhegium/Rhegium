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

import org.junit.Test;
import org.mockito.Mockito;
import org.rhegium.api.injector.AnnotatedInterfaceMatcher;
import org.rhegium.api.security.Permission;
import org.rhegium.api.security.PermissionAllowed;
import org.rhegium.api.security.PermissionDeniedException;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.RequiresPermission;
import org.rhegium.api.security.SecurityGroup;
import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;
import org.rhegium.api.security.authenticator.AuthenticationService;
import org.rhegium.api.security.spi.PermissionResolver;
import org.rhegium.api.security.spi.PrincipalFactory;
import org.rhegium.api.security.spi.SecurityGroupResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class SimplePermissionTestCase {

	@Test(expected = PermissionDeniedException.class)
	public void testSimplePermissionHandlingDenied() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(buildUserSession(principal));

		TestNoPermission instance = injector.getInstance(TestNoPermission.class);
		instance.test();
	}

	@Test
	public void testSimplePermissionHandlingGranted() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(buildUserSession(principal));

		TestAlwaysGrant instance = injector.getInstance(TestAlwaysGrant.class);
		instance.test();
	}

	@Test(expected = PermissionDeniedException.class)
	public void testInterfacePermissionHandlingDenied() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(buildUserSession(principal));

		TestInterface instance = injector.getInstance(TestInterface.class);
		instance.fail();
	}

	@Test
	public void testInterfacePermissionHandlingGranted() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(buildUserSession(principal));

		TestInterface instance = injector.getInstance(TestInterface.class);
		instance.success();
	}

	@SuppressWarnings("unchecked")
	private UserSession<Object> buildUserSession(Principal principal) {
		UserSession<Object> userSession = Mockito.mock(UserSession.class);
		Mockito.when(userSession.getPrincipal()).thenReturn(principal);
		return userSession;
	}

	private class PermissionModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(SecurityGroupResolver.class).to(TestSecurityGroupResolver.class).in(Singleton.class);
			bind(PermissionResolver.class).to(TestPermissionResolver.class).in(Singleton.class);
			bind(SecurityService.class).to(DefaultSecurityService.class).asEagerSingleton();
			bind(AuthenticationService.class).toInstance(Mockito.mock(AuthenticationService.class));

			bind(TestNoPermission.class);
			bind(TestAlwaysGrant.class);

			bind(TestInterface.class).to(TestInterfaceImpl.class);

			SecurityInterceptor securityInterceptor = new SecurityInterceptor();
			requestInjection(securityInterceptor);

			bindInterceptor(Matchers.any(), new AnnotatedInterfaceMatcher(RequiresPermission.class), securityInterceptor);

			bind(PrincipalFactory.class).to(DefaultPrincipalFactory.class).asEagerSingleton();
		}
	}

	public static class TestPermissionResolver implements PermissionResolver {

		@Override
		public Permission[] resolvePrincipalPermissions(Principal principal) {
			return new Permission[0];
		}

		@Override
		public Permission[] resolveGroupPermissions(SecurityGroup group) {
			return new Permission[0];
		}

		@Override
		public Permission resolvePermission(Class<? extends Permission> permission) {
			return null;
		}
	}

	public static class TestSecurityGroupResolver implements SecurityGroupResolver {

		@Inject
		private PermissionResolver permissionResolver;

		@Override
		public SecurityGroup resolveSecurityGroup(Principal principal) {
			return new DefaultSecurityGroup(permissionResolver, this, 1000, "Guest", -1);
		}

		@Override
		public SecurityGroup resolveSecurityGroup(long securityGroupId) {
			return null;
		}
	}

	public static class TestNoPermission {

		@RequiresPermission
		public void test() {
		}
	}

	public static class TestAlwaysGrant {

		@RequiresPermission(PermissionAllowed.class)
		public void test() {
		}
	}

	public static interface TestInterface {

		@RequiresPermission
		void fail();

		@RequiresPermission(PermissionAllowed.class)
		void success();
	}

	public static class TestInterfaceImpl implements TestInterface {

		@Override
		public void fail() {
		}

		@Override
		public void success() {
		}
	}

}
