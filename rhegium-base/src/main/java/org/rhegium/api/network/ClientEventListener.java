package org.rhegium.api.network;

public interface ClientEventListener {

	void clientConnected();

	void clientClosed();

	void clientDisconnected();

	void exceptionCaught(Throwable throwable) throws Throwable;

	void onMessage(Message message);

}
