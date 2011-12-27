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
package org.rhegium.vaadin.api.mvc;

import java.util.Collection;

import org.rhegium.api.mvc.AbstractController;
import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;
import org.rhegium.api.security.Permission;

import com.vaadin.ui.AbstractComponent;

public abstract class AbstractVaadinController<CC extends Controller<AbstractComponent, CC, V>, V extends View<AbstractComponent, CC, V>>
		extends AbstractController<AbstractComponent, CC, V> {

	public AbstractVaadinController(Class<? extends V> viewClass, boolean multiViewCapable) {
		super(viewClass, multiViewCapable);
	}

	public AbstractVaadinController(Class<? extends V> viewClass, Collection<Class<? extends Permission>> permissions,
			boolean multiViewCapable) {

		super(viewClass, permissions, multiViewCapable);
	}

	public AbstractVaadinController(Class<? extends V> viewClass, Collection<Class<? extends Permission>> permissions) {
		super(viewClass, permissions);
	}

	public AbstractVaadinController(Class<? extends V> viewClass) {
		super(viewClass);
	}

}
