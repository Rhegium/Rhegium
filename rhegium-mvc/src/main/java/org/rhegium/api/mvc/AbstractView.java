package org.rhegium.api.mvc;

import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;

import com.google.inject.Inject;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractView<C extends ComponentController<C, B>, B extends View<C, B>> implements View<C, B> {

	@Inject
	private SecurityService securityService;

	private C componentController;
	private VerticalLayout layout;

	@Override
	public void setComponentController(C componentController) {
		this.componentController = componentController;
	}

	@Override
	public C getComponentController() {
		return componentController;
	}

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
	public boolean isVisible() {
		UserSession<?> userSession = securityService.getUserSession();

		if (userSession == null || userSession.getPrincipal() == null) {
			return false;
		}

		return false;
	}

	protected abstract AbstractComponent buildComponent();

}
