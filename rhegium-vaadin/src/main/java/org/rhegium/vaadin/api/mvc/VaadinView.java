package org.rhegium.vaadin.api.mvc;

import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;

import com.vaadin.ui.AbstractComponent;

public interface VaadinView<CC extends Controller<AbstractComponent, CC, V>, V extends View<AbstractComponent, CC, V>> extends
		View<AbstractComponent, CC, V> {

}
