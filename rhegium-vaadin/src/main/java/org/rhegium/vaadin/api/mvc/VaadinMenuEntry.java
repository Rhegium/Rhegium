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

import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.MenuCategory;
import org.rhegium.api.mvc.MenuEntry;
import org.rhegium.api.mvc.View;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;

public class VaadinMenuEntry<CC extends Controller<AbstractComponent, CC, V>, V extends View<AbstractComponent, CC, V>> extends
		MenuEntry<AbstractComponent, CC, V> {

	public VaadinMenuEntry(CC controller, MenuCategory<?> category) {
		super(controller, category);
	}

	@Override
	protected AbstractComponent buildLabel(String title) {
		return new Label(title);
	}

}
