package org.rhegium.api.mvc;

import java.util.HashMap;
import java.util.Map;

import org.rhegium.api.uibinder.InjectUi;

public abstract class AbstractBindableView<C, CC extends Controller<C, CC, V>, V extends View<C, CC, V>> extends
		AbstractView<C, CC, V> {

	@InjectUi("$$abstractBindableViewComponents$$")
	private Map<String, C> abstractBindableViewComponents = new HashMap<String, C>();

	@Override
	protected abstract C buildComponent();

	@SuppressWarnings("unchecked")
	public <COMPONENT extends C> COMPONENT findByName(String name) {
		return (COMPONENT) abstractBindableViewComponents.get(name);
	}

}
