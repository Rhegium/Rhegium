package org.rhegium.api.network;

import org.rhegium.api.network.socket.RemoteNetworkingClient;

public interface RemoteClientListener {

	void clientConnected(RemoteNetworkingClient remoteClient);

	void clientClosed(RemoteNetworkingClient remoteClient);

	void clientDisconnected(RemoteNetworkingClient remoteClient);

}
