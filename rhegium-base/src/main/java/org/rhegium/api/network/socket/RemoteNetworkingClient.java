package org.rhegium.api.network.socket;

import org.jboss.netty.channel.ChannelFuture;

public interface RemoteNetworkingClient extends SendingClient {

	boolean isConnected();

	boolean isWritable();

	boolean isReadable();

	ChannelFuture close();

}
