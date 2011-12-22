package org.rhegium.api.uibinder;

import java.util.Locale;

import com.vaadin.ui.Component;

import de.heldenreich.wcc.framework.mvc.ComponentController;
import de.heldenreich.wcc.framework.mvc.View;

public interface UiBinderService {

	<C extends ComponentController<C, V>, V extends View<C, V>> V bindView(V view, Locale locale);

	<C extends ComponentController<C, V>, V extends View<C, V>> V bindView(V view, String xml, Locale locale);

	<C extends Component> C bind(Class<C> componentClass, View<?, ?> view, Locale locale);

	<C extends Component> C bind(String componentClass, View<?, ?> view, Locale locale);

	boolean isBindable(String componentName);

	boolean isBindable(Class<?> componentClass);

}
