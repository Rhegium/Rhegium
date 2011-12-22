package org.rhegium.api.network.protocol;

import java.util.Collection;

import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.MessageHandler;
import org.rhegium.api.network.MessageListener;
import org.rhegium.api.network.socket.SendingClient;

public interface ProtocolMessageTypeHandler {

	<M extends Message> void registerMessageHandler(Class<M> messageClass);

	<M extends Message> void registerMessageHandler(Class<M> messageClass, MessageHandler<M> messageHandler);

	void addMessageListener(MessageListener listener);

	void removeMessageListener(MessageListener listener);

	Collection<MessageListener> getMessageListeners();

	Message handlePacket(ChannelBufferInputStream stream) throws Exception;

	void handleMessage(Message message, SendingClient client) throws Exception;

}
