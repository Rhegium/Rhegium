package org.rhegium.api.mvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.rhegium.api.i18n.LanguageService;
import org.rhegium.api.security.Permission;
import org.rhegium.api.security.PermissionAllowed;
import org.rhegium.api.security.Principal;
import org.rhegium.api.security.SecurityService;
import org.rhegium.api.security.UserSession;
import org.rhegium.api.security.spi.PermissionResolver;
import org.rhegium.api.uibinder.UiBinderService;

import com.google.inject.Inject;
import com.google.inject.Injector;

public abstract class AbstractComponentController<C extends ComponentController<C, B>, B extends View<C, B>> implements
		ComponentController<C, B> {

	@SuppressWarnings("unchecked")
	private static final Collection<Class<? extends Permission>> VISIBLE_TO_ALL = Collections
			.unmodifiableCollection(Arrays
					.<Class<? extends Permission>> asList(new Class[] { PermissionAllowed.class }));

	@Inject
	private SecurityService securityService;

	@Inject
	private ViewManager viewManager;

	@Inject
	private LanguageService languageService;

	@Inject
	private PermissionResolver permissionResolver;

	@Inject
	private UiBinderService binderService;

	@Inject
	private Injector injector;

	private final Collection<Class<? extends Permission>> permissions;
	private final Class<? extends B> viewClass;
	private final boolean multiViewCapable;

	public AbstractComponentController(Class<? extends B> viewClass) {
		this.viewClass = viewClass;
		this.permissions = VISIBLE_TO_ALL;
		this.multiViewCapable = false;
	}

	public AbstractComponentController(Class<? extends B> viewClass, boolean multiViewCapable) {
		this.viewClass = viewClass;
		this.permissions = VISIBLE_TO_ALL;
		this.multiViewCapable = multiViewCapable;
	}

	public AbstractComponentController(Class<? extends B> viewClass, Collection<Class<? extends Permission>> permissions) {
		this.viewClass = viewClass;
		this.permissions = permissions;
		this.multiViewCapable = false;
	}

	public AbstractComponentController(Class<? extends B> viewClass,
			Collection<Class<? extends Permission>> permissions, boolean multiViewCapable) {

		this.viewClass = viewClass;
		this.permissions = permissions;
		this.multiViewCapable = multiViewCapable;
	}

	@Override
	public String getControllerName() {
		String className = getClass().getSimpleName();
		if (className.contains("$$EnhancerByGuice$$")) {
			return className.substring(0, className.indexOf("$$"));
		}

		return className;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B createView() {
		B view = injector.getInstance(viewClass);
		view.setComponentController((C) this);
		binderService.bindView(view, securityService.getUserSession().getLocale());
		return view;
	}

	@Override
	public boolean isPermitted(Principal principal) {
		for (Class<? extends Permission> permission : permissions) {
			Permission instance = permissionResolver.resolvePermission(permission);
			if (!principal.isPermitted(instance.getName(), instance.isPermittedByDefault())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isMultiViewCapable() {
		return multiViewCapable;
	}

	protected UserSession<?> getUserSession() {
		return securityService.getUserSession();
	}

	protected ViewManager getViewManager() {
		return viewManager;
	}

	protected LanguageService getLanguageService() {
		return languageService;
	}

}
