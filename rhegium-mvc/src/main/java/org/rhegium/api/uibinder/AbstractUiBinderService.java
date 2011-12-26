package org.rhegium.api.uibinder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.rhegium.api.i18n.LanguageService;
import org.rhegium.api.mvc.AbstractBindableView;
import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;
import org.rhegium.api.typeconverter.TypeConverterManager;
import org.rhegium.internal.utils.StringUtils;

import com.google.inject.Inject;

public abstract class AbstractUiBinderService<C> implements UiBinderService<C> {

	private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

	@Inject
	private LanguageService languageService;

	@Inject
	private TypeConverterManager typeConverterManager;

	@Inject
	private UiBinderEventService uiBinderEventService;

	public AbstractUiBinderService() {
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setValidating(false);
	}

	@Override
	public <CC extends Controller<C, CC, V>, V extends View<C, CC, V>> V bindView(V view, Locale locale) {
		if (!(view instanceof AbstractBindableView)) {
			return view;
		}

		// Remove all existing components from view
		C content = prepareView(view);

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
	public <CC extends Controller<C, CC, V>, V extends View<C, CC, V>> V bindView(V view, String xml, Locale locale) {
		if (!(view instanceof AbstractBindableView)) {
			return view;
		}

		// Remove all existing components from view
		C content = prepareView(view);

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
	public C bind(Class<? extends C> componentClass, View<C, ?, ?> view, Locale locale) {
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
	public C bind(String componentName, View<C, ?, ?> view, Locale locale) {
		C component = newBaseComposite();
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
	private Class<? extends C> getComponentClass(String componentName) {
		try {
			return (Class<? extends C>) Class.forName(componentName);
		}
		catch (ClassNotFoundException e) {
		}

		return null;
	}

	protected TypeConverterManager getTypeConverterManager() {
		return typeConverterManager;
	}

	protected LanguageService getLanguageService() {
		return languageService;
	}

	protected UiBinderEventService getUiBinderEventService() {
		return uiBinderEventService;
	}

	protected SAXParserFactory getSaxParserFactory() {
		return saxParserFactory;
	}

	protected abstract C bind(C component, InputStream stream, Object injectee, View<C, ?, ?> view, Locale locale);

	protected abstract C newBaseComposite();

	protected abstract C prepareView(View<C, ?, ?> view);

	private String resolveXmlDefinition(Class<?> clazz) {
		String canonicalName = clazz.getCanonicalName();
		return getViewXmlResource(canonicalName);
	}

	protected void injectBindings(Object injectee, Map<String, C> components) {
		injectBindings(injectee, components, injectee.getClass());
	}

	private void injectBindings(Object injectee, Map<String, C> components, Class<?> injecteeClass) {
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
					C component = components.get(property);
					if (component == null) {
						throw new UiBinderException("UiField " + field.getName() + " on type " + injecteeClass.getCanonicalName()
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
