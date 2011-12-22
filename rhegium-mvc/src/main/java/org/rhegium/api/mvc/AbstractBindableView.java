package org.rhegium.api.mvc;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import de.heldenreich.wcc.framework.mvc.uibinder.InjectUi;

public abstract class AbstractBindableView<C extends ComponentController<C, B>, B extends View<C, B>> extends
		AbstractView<C, B> {

	@InjectUi("$$abstractBindableViewComponents$$")
	private Map<String, Component> abstractBindableViewComponents = new HashMap<String, Component>();

	@Override
	protected final AbstractComponent buildComponent() {
		return new VerticalLayout();
	}

	@SuppressWarnings("unchecked")
	public <COMPONENT extends Component> COMPONENT findByName(String name) {
		return (COMPONENT) abstractBindableViewComponents.get(name);
	}

}
