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
