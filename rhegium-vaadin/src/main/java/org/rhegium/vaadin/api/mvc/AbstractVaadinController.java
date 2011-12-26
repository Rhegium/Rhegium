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
