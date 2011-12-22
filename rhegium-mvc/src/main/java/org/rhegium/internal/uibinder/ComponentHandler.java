package org.rhegium.internal.uibinder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.rhegium.api.mvc.View;
import org.rhegium.api.typeconverter.TypeConverterManager;
import org.rhegium.api.uibinder.UiBinderException;
import org.rhegium.internal.utils.ReflectionUtils;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;

class ComponentHandler implements TargetHandler {

	private final Map<String, String> packages = new HashMap<String, String>();
	private final Stack<Component> components = new Stack<Component>();
	private final Map<Component, Float> expandRatios = new HashMap<Component, Float>();

	private final TypeConverterManager typeConverterManager;
	private final DefaultUiBinderService binderService;
	private final View<?, ?> view;
	private final Locale locale;

	private Component currentComponent = null;

	ComponentHandler(DefaultUiBinderService binderService, View<?, ?> view, Locale locale,
			TypeConverterManager typeConverterManager) {

		this.typeConverterManager = typeConverterManager;
		this.binderService = binderService;
		this.view = view;
		this.locale = locale;

		this.currentComponent = view.getComponent();
	}

	@Override
	public String getTargetNamespace() {
		return null;
	}

	@Override
	public void handleStartElement(String uri, String name) {
		components.push(currentComponent);

		String packageName = getPackageNameByUri(uri);
		String viewName = packageName + "." + name;
		try {
			if (binderService.isBindable(viewName)) {
				currentComponent = binderService.bind(viewName, view, locale);
			}
			else {
				Class<?> clazz = Class.forName(viewName);
				currentComponent = (Component) clazz.newInstance();
			}
		}
		catch (ClassNotFoundException e) {
			throw new UiBinderException(e);
		}
		catch (InstantiationException e) {
			throw new UiBinderException(e);
		}
		catch (IllegalAccessException e) {
			throw new UiBinderException(e);
		}
	}

	@Override
	public void handleEndElement(String uri, String name) {
		Component innerComponent = currentComponent;
		currentComponent = components.pop();

		if (currentComponent instanceof Panel && innerComponent instanceof ComponentContainer) {
			((Panel) currentComponent).setContent((ComponentContainer) innerComponent);
		}
		else if (currentComponent instanceof ComponentContainer) {
			((ComponentContainer) currentComponent).addComponent(innerComponent);
		}

		if (expandRatios.size() > 0) {
			Iterator<Entry<Component, Float>> iterator = expandRatios.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Component, Float> entry = iterator.next();

				Component expandingComponent = entry.getKey();
				if (innerComponent != expandingComponent) {
					continue;
				}

				Component parentComponent = expandingComponent.getParent();

				if (!(parentComponent instanceof AbstractOrderedLayout)) {
					throw new UiBinderException("Cannot set expandRatio for element " + expandingComponent
							+ " since parent is no OrderedLayout");
				}

				Float expandRatio = entry.getValue();
				((AbstractOrderedLayout) parentComponent).setExpandRatio(expandingComponent, expandRatio);
				iterator.remove();
			}
		}
	}

	@Override
	public void handleAttribute(String name, Object value) {
		if (currentComponent == null) {
			return;
		}

		if ("expandRatio".equals(name)) {
			expandRatios.put(currentComponent, Float.parseFloat(value.toString()));
		}
		else {
			ReflectionUtils.injectValue(name, value, currentComponent, typeConverterManager);
		}
	}

	Component getCurrentComponent() {
		return currentComponent;
	}

	Component getRootComponent() {
		return components.get(0);
	}

	private String getPackageNameByUri(String uri) {
		if (packages.containsKey(uri)) {
			return packages.get(uri);
		}

		try {
			URI namespace = new URI(uri);
			if ("urn".equals(namespace.getScheme())) {
				String packageName = namespace.getSchemeSpecificPart();
				if (packageName.startsWith("import:")) {
					packageName = packageName.substring(7);
				}

				packages.put(uri, packageName);

				return packageName;
			}

			throw new UiBinderException("Illegal URI scheme for uri: " + uri);
		}
		catch (URISyntaxException e) {
			throw new UiBinderException(e);
		}
	}

}
