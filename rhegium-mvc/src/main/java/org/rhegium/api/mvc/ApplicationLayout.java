package org.rhegium.api.mvc;

public interface ApplicationLayout<W> {

	ApplicationLayout<W> init();

	void addCategory(MenuCategory<?> category);

	<C, CC extends Controller<C, CC, V>, V extends View<C, CC, V>> MenuCategory<?> createMenu(Controller<C, CC, V> controller);

	void closeView(View<?, ?, ?> view);

	void addSubWindow(W subWindow);

}
