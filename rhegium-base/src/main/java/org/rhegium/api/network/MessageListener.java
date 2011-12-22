package org.rhegium.api.network;

import org.rhegium.api.network.socket.SendingClient;

public interface MessageListener {

	void messageReceived(MessageType messageType, Message message, SendingClient client);

}
