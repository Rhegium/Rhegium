package org.rhegium.api.network;

import org.rhegium.api.network.socket.SendingClient;

public interface MessageHandler<M extends Message> {

	void handleMessage(M message, SendingClient client) throws Exception;

}
