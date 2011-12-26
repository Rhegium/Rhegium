package org.rhegium.api.mvc;

public abstract class MenuEntry<C, CC extends Controller<C, CC, V>, V extends View<C, CC, V>> {

	private final MenuCategory<?> category;
	private final CC controller;
	private final C label;
	private final String title;

	public MenuEntry(CC controller, MenuCategory<?> category) {
		this.controller = controller;
		this.category = category;

		this.label = buildLabel(controller.getTitle());
		this.title = controller.getTitle();
	}

	public MenuCategory<?> getCategory() {
		return category;
	}

	public String getTitle() {
		return title;
	}

	public CC getController() {
		return controller;
	}

	public C getLabel() {
		return label;
	}

	protected abstract C buildLabel(String title);

}
