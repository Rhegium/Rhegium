package org.rhegium.api.network.processor;

import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.socket.NetworkingClient;

public interface MessageDispatcher {

	void addMessageEvent(long messageId, MessageReceivedListener listener);

	<K extends AbstractMessage, V extends Message> MessageFuture<K, V> createMessageProcessor(
			NetworkingClient serviceClient);

	public static interface MessageReceivedListener {

		void messageReceived(Message message);

	}

}
