package org.rhegium.internal.uibinder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rhegium.api.i18n.LanguageService;
import org.rhegium.api.mvc.AbstractBindableView;
import org.rhegium.api.mvc.ComponentController;
import org.rhegium.api.mvc.View;
import org.rhegium.api.typeconverter.TypeConverterManager;
import org.rhegium.api.uibinder.InitializableView;
import org.rhegium.api.uibinder.InjectUi;
import org.rhegium.api.uibinder.UiBindable;
import org.rhegium.api.uibinder.UiBinderEventService;
import org.rhegium.api.uibinder.UiBinderException;
import org.rhegium.api.uibinder.UiBinderService;
import org.rhegium.internal.utils.StringUtils;
import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

class DefaultUiBinderService implements UiBinderService {

	private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

	@Inject
	private LanguageService languageService;

	@Inject
	private TypeConverterManager typeConverterManager;

	@Inject
	private UiBinderEventService uiBinderEventService;

	DefaultUiBinderService() {
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setValidating(false);
	}

	@Override
	public <C extends ComponentController<C, V>, V extends View<C, V>> V bindView(V view, Locale locale) {
		if (!(view instanceof AbstractBindableView)) {
			return view;
		}

		// Get view context pane
		VerticalLayout content = (VerticalLayout) view.getComponent();

		// Remove all existing components since we read the content from xml
		content.removeAllComponents();

		// Get XML definition stream
		String xmlDefinition = resolveXmlDefinition(view.getClass());
		InputStream stream = getResource(xmlDefinition);

		// Build up the Component tree
		bind(content, stream, view, view, locale);

		// Initialize view
		if (view instanceof InitializableView) {
			((InitializableView) view).initializeView();
		}

		// Return filled view
		return view;
	}

	@Override
	public <C extends ComponentController<C, V>, V extends View<C, V>> V bindView(V view, String xml, Locale locale) {
		if (!(view instanceof AbstractBindableView)) {
			return view;
		}

		// Get view context pane
		VerticalLayout content = (VerticalLayout) view.getComponent();

		// Remove all existing components since we read the content from xml
		content.removeAllComponents();

		// Get XML definition stream
		InputStream stream = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));

		// Build up the Component tree
		bind(content, stream, view, view, locale);

		// Initialize view
		if (view instanceof InitializableView) {
			((InitializableView) view).initializeView();
		}

		// Return filled view
		return view;
	}

	@Override
	public <C extends Component> C bind(Class<C> componentClass, View<?, ?> view, Locale locale) {
		try {
			C component = componentClass.newInstance();
			String xmlDefinition = resolveXmlDefinition(componentClass);
			InputStream stream = getResource(xmlDefinition);
			return bind(component, stream, component, view, locale);
		}
		catch (InstantiationException e) {
			throw new UiBinderException(e);
		}
		catch (IllegalAccessException e) {
			throw new UiBinderException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Component> C bind(String componentName, View<?, ?> view, Locale locale) {
		C component = (C) new BaseComposite();
		String xmlDefinition = getViewXmlResource(componentName);
		InputStream stream = getResource(xmlDefinition);
		return bind(component, stream, component, view, locale);
	}

	@Override
	public boolean isBindable(String componentName) {
		Class<?> viewClass = getComponentClass(componentName);
		if (viewClass != null) {
			return isBindable(viewClass);
		}

		InputStream resource = getResource(getViewXmlResource(componentName));
		return resource != null;
	}

	@Override
	public boolean isBindable(Class<?> componentClass) {
		return UiBindable.class.isAssignableFrom(componentClass);
	}

	private String getViewXmlResource(String viewName) {
		return viewName.replace(".", "/") + ".xml";
	}

	private InputStream getResource(String xmlDefinition) {
		return getClass().getClassLoader().getResourceAsStream(xmlDefinition);
	}

	@SuppressWarnings("unchecked")
	private <C extends Component> Class<C> getComponentClass(String componentName) {
		try {
			return (Class<C>) Class.forName(componentName);
		}
		catch (ClassNotFoundException e) {
		}

		return null;
	}

	private <C extends Component> C bind(C component, InputStream stream, Object injectee, View<?, ?> view,
			Locale locale) {

		try {
			// Retrieve SAX parser instance
			SAXParser saxParser = saxParserFactory.newSAXParser();

			// Initialize TargetHandlers
			ComponentHandler componentHandler = new ComponentHandler(this, view, locale, typeConverterManager);
			ComponentIdHandler componentIdHandler = new ComponentIdHandler(componentHandler);
			ResourceHandler resourceHandler = new ResourceHandler(componentHandler, component.getApplication());
			EventBusHandler eventBusHandler = new EventBusHandler(view, componentHandler, uiBinderEventService);
			LanguageBindingHandler languageBindingHandler = new LanguageBindingHandler(languageService,
					componentHandler, locale);

			// Initialize SAX handler
			UiBinderSaxHandler saxHandler = new UiBinderSaxHandler(componentHandler, componentIdHandler,
					resourceHandler, eventBusHandler, languageBindingHandler);

			// Parse stream
			saxParser.parse(stream, saxHandler);

			// Inject bindings
			injectBindings(injectee, componentIdHandler.getComponents());

			return component;
		}
		catch (ParserConfigurationException e) {
			throw new UiBinderException(e);
		}
		catch (SAXException e) {
			throw new UiBinderException(e);
		}
		catch (IOException e) {
			throw new UiBinderException(e);
		}
	}

	private String resolveXmlDefinition(Class<?> clazz) {
		String canonicalName = clazz.getCanonicalName();
		return getViewXmlResource(canonicalName);
	}

	private void injectBindings(Object injectee, Map<String, Component> components) {
		injectBindings(injectee, components, injectee.getClass());
	}

	private void injectBindings(Object injectee, Map<String, Component> components, Class<?> injecteeClass) {
		// Synchronize to force context update after injection to make fields
		// available to all others threads
		synchronized (injectee) {
			for (Field field : injecteeClass.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				if (!field.isAnnotationPresent(InjectUi.class)) {
					continue;
				}

				InjectUi injectUi = field.getAnnotation(InjectUi.class);
				String property = StringUtils.isEmpty(injectUi.value()) ? field.getName() : injectUi.value();

				if ("$$abstractBindableViewComponents$$".equals(property)) {
					setValue(field, injectee, components);
				}
				else {
					Component component = components.get(property);
					if (component == null) {
						throw new UiBinderException("UiField " + field.getName() + " on type "
								+ injecteeClass.getCanonicalName()
								+ " could not be injected since no component with id " + property + " exists");
					}
					setValue(field, injectee, component);
				}
			}

			if (injecteeClass.getSuperclass() != null && injecteeClass.getSuperclass() != Object.class) {
				injectBindings(injectee, components, injecteeClass.getSuperclass());
			}
		}
	}

	private void setValue(Field field, Object injectee, Object value) {
		try {
			field.setAccessible(true);
			field.set(injectee, value);
		}
		catch (IllegalAccessException e) {
			throw new UiBinderException(e);
		}
	}

}
