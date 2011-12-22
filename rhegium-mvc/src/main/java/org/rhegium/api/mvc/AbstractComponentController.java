package org.rhegium.api.mvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.jboss.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.heldenreich.wcc.commons.net.AbstractMessage;
import de.heldenreich.wcc.commons.net.Message;
import de.heldenreich.wcc.commons.net.client.ServiceClient;
import de.heldenreich.wcc.commons.net.client.processor.MessageDispatcher;
import de.heldenreich.wcc.commons.net.client.processor.MessageDispatcher.MessageReceivedListener;
import de.heldenreich.wcc.commons.net.client.processor.MessageFuture;
import de.heldenreich.wcc.framework.i18n.LanguageService;
import de.heldenreich.wcc.framework.mvc.uibinder.UiBinderService;
import de.heldenreich.wcc.framework.security.PermissionAllowed;
import de.heldenreich.wcc.framework.security.PermissionResolver;
import de.heldenreich.wcc.web.commons.gameserver.GameServerDescriptor;
import de.heldenreich.wcc.web.commons.gameserver.GameServerService;
import de.heldenreich.wcc.web.commons.security.Permission;
import de.heldenreich.wcc.web.commons.security.Principal;
import de.heldenreich.wcc.web.commons.security.SecurityService;
import de.heldenreich.wcc.web.commons.security.UserSession;

public abstract class AbstractComponentController<C extends ComponentController<C, B>, B extends View<C, B>> implements
		ComponentController<C, B> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractComponentController.class);

	@SuppressWarnings("unchecked")
	private static final Collection<Class<? extends Permission>> VISIBLE_TO_ALL = Collections
			.unmodifiableCollection(Arrays
					.<Class<? extends Permission>> asList(new Class[] { PermissionAllowed.class }));

	@Inject
	private GameServerService gameServerService;

	@Inject
	private SecurityService securityService;

	@Inject
	private ViewManager viewManager;

	@Inject
	private MessageDispatcher messageDispatcher;

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
	public <K extends AbstractMessage, V extends Message> MessageFuture<K, V> createMessageProcessor() {
		return messageDispatcher.createMessageProcessor(getServiceClient());
	}

	@Override
	public ChannelFuture sendMessageAsync(Message message) {
		if (message instanceof AbstractMessage) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Async sending message " + message + "...");
			}

			AbstractMessage request = (AbstractMessage) message;
			request = AbstractMessage.addRequestMessageId(request);

			// Async handling of incoming response messages
			messageDispatcher.addMessageEvent(request.getMessageId(), new MessageReceivedListener() {

				@Override
				public void messageReceived(Message message) {
					// Prepare async UI changes pushing and handle message
					prepareMessageHandling(message);
				}
			});
			return getServiceClient().sendMessage(request);
		}

		return getServiceClient().sendMessage(message);
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

	protected ServiceClient getServiceClient() {
		UserSession<?> userSession = getUserSession();
		GameServerDescriptor descriptor = userSession.getSelectedGameServerDescriptor();
		return gameServerService.getServiceClient(descriptor);
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

	private void prepareMessageHandling(Message message) {
		// TODO View handling
		onMessage(message, null);
	}

	protected void onMessage(Message message, B view) {
	}

}
