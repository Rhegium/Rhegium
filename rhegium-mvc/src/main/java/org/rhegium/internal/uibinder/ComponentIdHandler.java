package org.rhegium.internal.uibinder;

import java.util.HashMap;
import java.util.Map;

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
