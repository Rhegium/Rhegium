package org.rhegium.vaadin.api.mvc;

import org.rhegium.api.mvc.AbstractView;
import org.rhegium.api.mvc.Controller;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractVaadinView<CC extends Controller<AbstractComponent, CC, V>, V extends VaadinView<CC, V>> extends
		AbstractView<AbstractComponent, CC, V> {

	private VerticalLayout layout;

	@Override
	public synchronized final AbstractComponent getComponent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.setSizeFull();
			layout.setMargin(true);
			layout.addComponent(buildComponent());
		}

		return layout;
	}

}
