package org.rhegium.vaadin.api.mvc;

import org.rhegium.api.mvc.AbstractBindableView;
import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractVaadinBindableView<CC extends Controller<AbstractComponent, CC, V>, V extends View<AbstractComponent, CC, V>>
		extends AbstractBindableView<AbstractComponent, CC, V> {

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

	@Override
	protected AbstractComponent buildComponent() {
		return new VerticalLayout();
	}

}
