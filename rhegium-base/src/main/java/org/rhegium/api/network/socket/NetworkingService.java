package org.rhegium.api.network.socket;

import java.util.concurrent.Executor;

import org.rhegium.api.network.protocol.PacketTypeInitializer;

public interface NetworkingService {

	NetworkingClient buildNetworkingClient(Executor executor, PacketTypeInitializer packetTypeInitializer);

	NetworkingServer buildNetworkingServer(String address, int port, Executor executor,
			PacketTypeInitializer initializer);

}
