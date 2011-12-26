package org.rhegium.api.network;

import org.rhegium.api.network.socket.SendingClient;

public interface AsyncMessageListener extends MessageListener {

	void messageFailure(Message message, Exception exception, SendingClient client);

}
