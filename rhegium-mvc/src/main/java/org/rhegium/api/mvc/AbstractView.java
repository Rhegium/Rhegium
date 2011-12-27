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
package org.rhegium.api.mvc;

import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;

import com.google.inject.Inject;

public abstract class AbstractView<C, CC extends Controller<C, CC, V>, V extends View<C, CC, V>> implements View<C, CC, V> {

	@Inject
	private SecurityService securityService;

	private CC componentController;

	@Override
	public void setComponentController(CC componentController) {
		this.componentController = componentController;
	}

	@Override
	public CC getComponentController() {
		return componentController;
	}

	@Override
	public boolean isVisible() {
		UserSession<?> userSession = securityService.getUserSession();

		if (userSession == null || userSession.getPrincipal() == null) {
			return false;
		}

		return false;
	}

	protected abstract C buildComponent();

}
