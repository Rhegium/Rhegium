package org.rhegium.api.mvc;

import com.vaadin.ui.Label;

public class MenuEntry<C extends ComponentController<C, B>, B extends View<C, B>> {

	private final MenuCategory category;
	private final C controller;
	private final Label label;
	private final String title;

	public MenuEntry(C controller, MenuCategory category) {
		this.controller = controller;
		this.category = category;

		this.label = new Label(controller.getTitle());
		this.title = controller.getTitle();
	}

	public MenuCategory getCategory() {
		return category;
	}

	public String getTitle() {
		return title;
	}

	public C getController() {
		return controller;
	}

	public Label getLabel() {
		return label;
	}

}
