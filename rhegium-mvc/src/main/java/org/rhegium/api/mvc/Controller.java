package org.rhegium.api.mvc;

import org.rhegium.api.security.Principal;

public interface Controller<C, CC extends Controller<C, CC, V>, V extends View<C, CC, V>> {

	V createView();

	String getTitle();

	String getControllerName();

	String getMenuCategory();

	boolean isPermitted(Principal principal);

	boolean isMultiViewCapable();

}
