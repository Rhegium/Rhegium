package org.rhegium.vaadin.api.mvc;

import org.rhegium.api.mvc.MenuCategory;

import com.vaadin.ui.VerticalLayout;

public class VaadinMenuCategory extends MenuCategory<VerticalLayout> {

	public VaadinMenuCategory(VerticalLayout content, String title) {
		super(content, title);
	}

}
