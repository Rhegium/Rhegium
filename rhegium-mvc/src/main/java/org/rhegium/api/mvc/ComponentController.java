package org.rhegium.api.mvc;

import org.jboss.netty.channel.ChannelFuture;
import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.processor.MessageFuture;
import org.rhegium.api.security.Principal;

public interface ComponentController<C extends ComponentController<C, B>, B extends View<C, B>> {

	B createView();

	<K extends AbstractMessage, V extends Message> MessageFuture<K, V> createMessageProcessor();

	ChannelFuture sendMessageAsync(Message message);

	String getTitle();

	String getControllerName();

	String getMenuCategory();

	boolean isPermitted(Principal principal);

	boolean isMultiViewCapable();

}
