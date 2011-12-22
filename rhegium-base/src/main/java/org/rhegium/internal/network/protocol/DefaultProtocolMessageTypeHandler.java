package org.rhegium.internal.network.protocol;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.MessageHandler;
import org.rhegium.api.network.MessageListener;
import org.rhegium.api.network.MessageType;
import org.rhegium.api.network.MessageTypeHandler;
import org.rhegium.api.network.ProtocolConfigurationException;
import org.rhegium.api.network.protocol.FailureMessage;
import org.rhegium.api.network.protocol.ProtocolMessageTypeHandler;
import org.rhegium.api.network.socket.SendingClient;

public class DefaultProtocolMessageTypeHandler implements ProtocolMessageTypeHandler {

	private final Map<MessageType, MessageTypeDefinition<?>> messageTypeDefinitions;
	private final Set<MessageListener> listeners = new HashSet<MessageListener>();

	public DefaultProtocolMessageTypeHandler() {
		messageTypeDefinitions = new HashMap<MessageType, MessageTypeDefinition<?>>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <M extends Message> void registerMessageHandler(Class<M> messageClass) {
		if (messageClass == null) {
			throw new ProtocolConfigurationException("messageClass cannot be null");
		}

		if (!messageClass.isAnnotationPresent(MessageTypeHandler.class)) {
			throw new ProtocolConfigurationException(
					"You cannot register messageType not annotated with MessageTypeHandler");
		}

		try {
			MessageTypeHandler typeHandler = messageClass.getAnnotation(MessageTypeHandler.class);
			registerMessageHandler(messageClass, (MessageHandler<M>) typeHandler.value().newInstance());
		}
		catch (Exception e) {
			throw new ProtocolConfigurationException("You cannot register messageClass", e);
		}
	}

	@Override
	public <M extends Message> void registerMessageHandler(Class<M> messageClass, MessageHandler<M> messageHandler) {
		if (messageClass == null) {
			throw new ProtocolConfigurationException("messageClass cannot be null");
		}

		if (messageHandler == null) {
			throw new ProtocolConfigurationException("messageHandler cannot be null");
		}

		try {
			Message message = messageClass.newInstance();
			MessageType messageType = message.getMessageType();
			messageTypeDefinitions.put(messageType, new MessageTypeDefinition<M>(messageType, messageClass,
					messageHandler));
		}
		catch (Exception e) {
			throw new ProtocolConfigurationException("You cannot register messageClass " + messageClass, e);
		}
	}

	@Override
	public Message handlePacket(ChannelBufferInputStream stream) throws Exception {
		final long messageTypeId = stream.readLong();
		final MessageType messageType = MessageType.byValue(messageTypeId);

		if (messageType == null) {
			throw new ProtocolConfigurationException("MessageType with id " + messageTypeId + " is not registered");
		}

		MessageTypeDefinition<?> definition = messageTypeDefinitions.get(messageType);
		try {
			return definition.getMessageClass().newInstance();
		}
		catch (Exception e) {
			// Just log the problem but do not throw it
			new ProtocolConfigurationException("Message class could not be instantiated", e).printStackTrace();
		}

		return null;
	}

	@Override
	public void handleMessage(Message message, SendingClient client) throws Exception {
		MessageType messageType = message.getMessageType();
		MessageTypeDefinition<?> definition = messageTypeDefinitions.get(messageType);

		if (definition == null) {
			// FailureMessage has totally different handling
			if (message instanceof FailureMessage) {
				return;
			}

			throw new ProtocolConfigurationException("No MessageHandler registered for MessageType " + messageType);
		}

		if (!definition.getMessageType().equals(messageType)) {
			throw new ProtocolConfigurationException("MessageHandlers do not equal");
		}

		if (!definition.getMessageClass().equals(message.getClass())) {
			throw new ProtocolConfigurationException("MessageHandler MessageType " + messageType
					+ " cannot handle messages of type " + message.getClass());
		}

		processMessage(message, client, definition);

		for (MessageListener messageListener : getMessageListeners()) {
			messageListener.messageReceived(message.getMessageType(), message, client);
		}
	}

	@Override
	public void addMessageListener(MessageListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMessageListener(MessageListener listener) {
		listeners.remove(listener);
	}

	@Override
	public Collection<MessageListener> getMessageListeners() {
		return Collections.unmodifiableCollection(listeners);
	}

	@SuppressWarnings("unchecked")
	private <M extends Message> void processMessage(M message, SendingClient client, MessageTypeDefinition<?> definition)
			throws Exception {

		MessageHandler<M> messageHandler = (MessageHandler<M>) definition.getMessageHandler();
		messageHandler.handleMessage(message, client);
	}

	private class MessageTypeDefinition<M extends Message> {

		private final MessageType messageType;
		private final Class<M> messageClass;
		private final MessageHandler<M> messageHandler;

		public MessageTypeDefinition(MessageType messageType, Class<M> messageClass, MessageHandler<M> messageHandler) {
			this.messageType = messageType;
			this.messageClass = messageClass;
			this.messageHandler = messageHandler;
		}

		public MessageType getMessageType() {
			return messageType;
		}

		public Class<M> getMessageClass() {
			return messageClass;
		}

		public MessageHandler<M> getMessageHandler() {
			return messageHandler;
		}

	}

}
