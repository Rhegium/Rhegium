package org.rhegium.api.mvc;

import org.rhegium.api.uibinder.UiBindable;

import com.vaadin.ui.AbstractComponent;

public interface View<C extends ComponentController<C, B>, B extends View<C, B>> extends UiBindable {

	void setComponentController(C componentController);

	C getComponentController();

	AbstractComponent getComponent();

	boolean isVisible();

}
