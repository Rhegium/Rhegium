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

import java.util.Locale;

import org.junit.Test;
import org.rhegium.api.injector.AnnotatedInterfaceMatcher;
import org.rhegium.api.security.AbstractPermission;
import org.rhegium.api.security.LogoutListener;
import org.rhegium.api.security.Permission;
import org.rhegium.api.security.PermissionDeniedException;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.RequiresPermission;
import org.rhegium.api.security.SecurityGroup;
import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;
import org.rhegium.api.security.authenticator.Authenticator;
import org.rhegium.api.security.spi.PermissionResolver;
import org.rhegium.api.security.spi.PrincipalFactory;
import org.rhegium.api.security.spi.SecurityGroupResolver;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class PermissionTestCase {

	@Test(expected = PermissionDeniedException.class)
	public void testFailBecauseOfExplicitDenied() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(new UserSession<Object>() {

			@Override
			public Principal getPrincipal() {
				return principal;
			}

			@Override
			public Object getNativeSession() {
				return null;
			}

			@Override
			public SecurityService getSecurityService() {
				return null;
			}

			@Override
			public boolean isAutoLogin() {
				return false;
			}

			@Override
			public boolean isAuthenticated() {
				return false;
			}

			@Override
			public void setLocale(Locale locale) {
			}

			@Override
			public Locale getLocale() {
				return null;
			}

			@Override
			public void logout(LogoutListener... logoutListeners) {
			}

			@Override
			public Authenticator getAuthenticator() {
				return null;
			}
		});

		TestNoPermission instance = injector.getInstance(TestNoPermission.class);
		instance.failBecauseOfExplicitDenied();
	}

	@Test
	public void testSuccessBecauseOfExplicitAllowed() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(new UserSession<Object>() {

			@Override
			public Principal getPrincipal() {
				return principal;
			}

			@Override
			public Object getNativeSession() {
				return null;
			}

			@Override
			public SecurityService getSecurityService() {
				return null;
			}

			@Override
			public boolean isAutoLogin() {
				return false;
			}

			@Override
			public boolean isAuthenticated() {
				return false;
			}

			@Override
			public void setLocale(Locale locale) {
			}

			@Override
			public Locale getLocale() {
				return null;
			}

			@Override
			public void logout(LogoutListener... logoutListeners) {
			}

			@Override
			public Authenticator getAuthenticator() {
				return null;
			}
		});

		TestNoPermission instance = injector.getInstance(TestNoPermission.class);
		instance.successBecauseOfExplicitAllowed();
	}

	@Test(expected = PermissionDeniedException.class)
	public void testFailBecauseOfImplicitlyDenied() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(new UserSession<Object>() {

			@Override
			public Principal getPrincipal() {
				return principal;
			}

			@Override
			public Object getNativeSession() {
				return null;
			}

			@Override
			public SecurityService getSecurityService() {
				return null;
			}

			@Override
			public boolean isAutoLogin() {
				return false;
			}

			@Override
			public boolean isAuthenticated() {
				return false;
			}

			@Override
			public void setLocale(Locale locale) {
			}

			@Override
			public Locale getLocale() {
				return null;
			}

			@Override
			public void logout(LogoutListener... logoutListeners) {
			}

			@Override
			public Authenticator getAuthenticator() {
				return null;
			}
		});

		TestNoPermission instance = injector.getInstance(TestNoPermission.class);
		instance.failBecauseOfImplicitlyDenied();
	}

	@Test
	public void testSuccessBecauseOfImplicitlyAllowed() {
		final Injector injector = Guice.createInjector(new PermissionModule());

		final SecurityService securityService = injector.getInstance(SecurityService.class);
		final PrincipalFactory factory = injector.getInstance(PrincipalFactory.class);
		final Principal principal = factory.create("Peter", 1000, new String[] { "Peter2", "Peter" });

		securityService.setUserSession(new UserSession<Object>() {

			@Override
			public Principal getPrincipal() {
				return principal;
			}

			@Override
			public Object getNativeSession() {
				return null;
			}

			@Override
			public SecurityService getSecurityService() {
				return null;
			}

			@Override
			public boolean isAutoLogin() {
				return false;
			}

			@Override
			public boolean isAuthenticated() {
				return false;
			}

			@Override
			public void setLocale(Locale locale) {
			}

			@Override
			public Locale getLocale() {
				return null;
			}

			@Override
			public void logout(LogoutListener... logoutListeners) {
			}

			@Override
			public Authenticator getAuthenticator() {
				return null;
			}
		});

		TestNoPermission instance = injector.getInstance(TestNoPermission.class);
		instance.successBecauseOfImplicitlyAllowed();
	}

	private class PermissionModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(SecurityGroupResolver.class).to(TestSecurityGroupResolver.class).in(Singleton.class);
			bind(PermissionResolver.class).to(TestPermissionResolver.class).in(Singleton.class);
			bind(SecurityService.class).to(DefaultSecurityService.class).asEagerSingleton();

			bind(TestNoPermission.class);

			SecurityInterceptor securityInterceptor = new SecurityInterceptor();
			requestInjection(securityInterceptor);

			bindInterceptor(Matchers.any(), new AnnotatedInterfaceMatcher(RequiresPermission.class), securityInterceptor);

			bind(PrincipalFactory.class).to(DefaultPrincipalFactory.class).asEagerSingleton();
		}
	}

	public static class TestPermissionResolver implements PermissionResolver {

		@Override
		public Permission[] resolvePrincipalPermissions(Principal principal) {
			return new Permission[] { new TestPermissionDenied(), new TestPermissionAllowed() };
		}

		@Override
		public Permission[] resolveGroupPermissions(SecurityGroup group) {
			return new Permission[] { new TestPermissionDenied(), new TestPermissionAllowed() };
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

		@RequiresPermission(value = TestPermissionDenied.class)
		public void failBecauseOfExplicitDenied() {
		}

		@RequiresPermission(value = TestPermissionAllowed.class)
		public void successBecauseOfExplicitAllowed() {
		}

		@RequiresPermission(values = { TestPermissionAllowed.class, TestPermissionNotGivenButDenied.class })
		public void failBecauseOfImplicitlyDenied() {
		}

		@RequiresPermission(values = { TestPermissionAllowed.class, TestPermissionNotGivenButAllowed.class })
		public void successBecauseOfImplicitlyAllowed() {
		}

	}

	public static class TestPermissionDenied extends AbstractPermission {

		@Override
		public boolean isPermittedByDefault() {
			return false;
		}

		@Override
		public String getGroup() {
			return null;
		}
	}

	public static class TestPermissionAllowed extends AbstractPermission {

		@Override
		public boolean isPermittedByDefault() {
			return true;
		}

		@Override
		public String getGroup() {
			return null;
		}
	}

	public static class TestPermissionNotGivenButAllowed extends AbstractPermission {

		@Override
		public boolean isPermittedByDefault() {
			return true;
		}

		@Override
		public String getGroup() {
			return null;
		}
	}

	public static class TestPermissionNotGivenButDenied extends AbstractPermission {

		@Override
		public boolean isPermittedByDefault() {
			return false;
		}

		@Override
		public String getGroup() {
			return null;
		}
	}

}
