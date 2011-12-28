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

import com.google.inject.AbstractModule;

public class SecurityResolverModule extends AbstractModule {

	@Override
	protected void configure() {
		// TODO: Add DefaultPermissionResolver
		// bind(PermissionResolver.class).to(DefaultPermissionResolver.class).in(Singleton.class);

		// TODO: Add DefaultPrincipalResolver
		// bind(PrincipalResolver.class).to(DefaultPrincipalResolver.class).in(Singleton.class);

		// TODO: Add DefaultSecurityGroupResolver
		// bind(SecurityGroupResolver.class).to(DefaultSecurityGroupResolver.class).in(Singleton.class);
	}

}
