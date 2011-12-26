package org.rhegium.api.uibinder;

import java.util.Locale;

import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;

public interface UiBinderService<C> {

	<CC extends Controller<C, CC, V>, V extends View<C, CC, V>> V bindView(V view, Locale locale);

	<CC extends Controller<C, CC, V>, V extends View<C, CC, V>> V bindView(V view, String xml, Locale locale);

	C bind(Class<? extends C> componentClass, View<C, ?, ?> view, Locale locale);

	C bind(String componentClass, View<C, ?, ?> view, Locale locale);

	boolean isBindable(String componentName);

	boolean isBindable(Class<?> componentClass);

}
