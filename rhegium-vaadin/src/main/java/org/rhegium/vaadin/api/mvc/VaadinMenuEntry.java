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
