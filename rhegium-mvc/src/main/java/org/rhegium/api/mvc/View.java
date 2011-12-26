package org.rhegium.api.mvc;

import org.rhegium.api.uibinder.UiBindable;

public interface View<C, CC extends Controller<C, CC, V>, V extends View<C, CC, V>> extends UiBindable {

	void setComponentController(CC componentController);

	CC getComponentController();

	C getComponent();

	boolean isVisible();

}
