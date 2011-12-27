/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
