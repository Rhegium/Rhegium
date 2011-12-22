package org.rhegium.api.network.socket;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.rhegium.api.network.RemoteClientListener;
import org.rhegium.api.network.protocol.ProtocolChannelPipelineFactory;

public interface NetworkingServer {

	void bind();

	ChannelFuture close();

	ProtocolChannelPipelineFactory getChannelPipelineFactory();

	void setChannelPipelineFactory(ProtocolChannelPipelineFactory channelPipelineFactory);

	void addRemoteClientListener(RemoteClientListener remoteClientListener);

	void removeRemoteClientListener(RemoteClientListener remoteClientListener);

	SimpleChannelUpstreamHandler getChannelUpstreamHandler();

}
