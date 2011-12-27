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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.socket.RemoteNetworkingClient;

class DefaultRemoteNetworkingClient implements RemoteNetworkingClient {

	private final Channel channel;

	DefaultRemoteNetworkingClient(Channel channel) {
		this.channel = channel;
	}

	@Override
	public ChannelFuture sendMessage(Message message) {
		return channel.write(message);
	}

	public boolean isConnected() {
		return channel.isConnected();
	}

	public boolean isWritable() {
		return channel.isWritable();
	}

	public boolean isReadable() {
		return channel.isReadable();
	}

	public ChannelFuture close() {
		return channel.close();
	}

	Channel getChannel() {
		return channel;
	}

}
