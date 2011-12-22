package org.rhegium.api.mvc;

import com.vaadin.ui.Window;

public interface ApplicationLayout {

	ApplicationLayout init();

	void addCategory(MenuCategory category);

	<C extends ComponentController<C, B>, B extends View<C, B>> MenuCategory createMenu(
			ComponentController<C, B> controller);

	void closeView(View<?, ?> view);

	void addSubWindow(Window subWindow);

}
