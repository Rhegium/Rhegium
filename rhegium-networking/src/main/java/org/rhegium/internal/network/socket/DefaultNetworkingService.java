package org.rhegium.internal.network.socket;

import java.util.concurrent.Executor;

import org.rhegium.api.network.protocol.PacketTypeInitializer;
import org.rhegium.api.network.socket.NetworkingClient;
import org.rhegium.api.network.socket.NetworkingServer;
import org.rhegium.api.network.socket.NetworkingService;

public class DefaultNetworkingService implements NetworkingService {

	@Override
	public NetworkingClient buildNetworkingClient(Executor executor, PacketTypeInitializer packetTypeInitializer) {
		return new DefaultNetworkingClient(executor, packetTypeInitializer);
	}

	@Override
	public NetworkingServer buildNetworkingServer(String address, int port, Executor executor,
			PacketTypeInitializer packetTypeInitializer) {

		return new DefaultNetworkingServer(address, port, executor, packetTypeInitializer);
	}

}
