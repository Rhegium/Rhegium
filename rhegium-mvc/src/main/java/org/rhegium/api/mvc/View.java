package org.rhegium.api.mvc;

import com.vaadin.ui.AbstractComponent;

import de.heldenreich.wcc.framework.mvc.uibinder.UiBindable;

public interface View<C extends ComponentController<C, B>, B extends View<C, B>> extends UiBindable {

	void setComponentController(C componentController);

	C getComponentController();

	AbstractComponent getComponent();

	boolean isVisible();

}
