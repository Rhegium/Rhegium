package org.rhegium.api.mvc;

import org.rhegium.api.security.Principal;

public interface ComponentController<C extends ComponentController<C, B>, B extends View<C, B>> {

	B createView();

	String getTitle();

	String getControllerName();

	String getMenuCategory();

	boolean isPermitted(Principal principal);

	boolean isMultiViewCapable();

}
