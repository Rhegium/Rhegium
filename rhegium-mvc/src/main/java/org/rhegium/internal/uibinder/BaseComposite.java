package org.rhegium.internal.uibinder;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
class BaseComposite extends VerticalLayout {

	private int count;

	@Override
	public void addComponent(Component c) {
		super.addComponent(c);
		count++;
	}

	@Override
	public void removeComponent(Component c) {
		super.removeComponent(c);
		count--;
	}

}
