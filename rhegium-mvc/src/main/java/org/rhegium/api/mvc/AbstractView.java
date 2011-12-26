package org.rhegium.api.mvc;

import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;

import com.google.inject.Inject;

public abstract class AbstractView<C, CC extends Controller<C, CC, V>, V extends View<C, CC, V>> implements View<C, CC, V> {

	@Inject
	private SecurityService securityService;

	private CC componentController;

	@Override
	public void setComponentController(CC componentController) {
		this.componentController = componentController;
	}

	@Override
	public CC getComponentController() {
		return componentController;
	}

	@Override
	public boolean isVisible() {
		UserSession<?> userSession = securityService.getUserSession();

		if (userSession == null || userSession.getPrincipal() == null) {
			return false;
		}

		return false;
	}

	protected abstract C buildComponent();

}
