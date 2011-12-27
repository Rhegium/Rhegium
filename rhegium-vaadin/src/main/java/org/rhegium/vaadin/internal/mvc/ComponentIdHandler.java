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
package org.rhegium.vaadin.internal.mvc;

import java.util.HashMap;
import java.util.Map;

import org.rhegium.api.uibinder.TargetHandler;
import org.rhegium.api.uibinder.UiBinderException;
import org.rhegium.internal.utils.StringUtils;

import com.vaadin.ui.Component;

class ComponentIdHandler implements TargetHandler {

	private final Map<String, Component> components = new HashMap<String, Component>();

	private final ComponentHandler componentHandler;

	ComponentIdHandler(ComponentHandler componentHandler) {
		this.componentHandler = componentHandler;
	}

	@Override
	public String getTargetNamespace() {
		return "urn:de.heldenreich.wcc.framework.mvc.uibinder";
	}

	@Override
	public void handleStartElement(String uri, String name) {
	}

	@Override
	public void handleEndElement(String uri, String name) {
	}

	@Override
	public void handleAttribute(String name, Object value) {
		if ("id".equals(name)) {
			if (value == null || !(value instanceof String) || StringUtils.isEmpty(value.toString())) {
				throw new UiBinderException("Illegal attribute value found");
			}

			if (components.containsKey(value)) {
				throw new UiBinderException("Duplicate component with id " + value + "found");
			}

			Component component = componentHandler.getCurrentComponent();
			components.put(value.toString(), component);
		}
		else {
			componentHandler.handleAttribute(name, value);
		}
	}

	Map<String, Component> getComponents() {
		return components;
	}

}
