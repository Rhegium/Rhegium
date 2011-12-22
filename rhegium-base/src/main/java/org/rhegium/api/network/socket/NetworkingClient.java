package org.rhegium.api.network.socket;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.rhegium.api.network.ClientEventListener;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.protocol.ProtocolChannelPipelineFactory;

public interface NetworkingClient extends SendingClient {

	ProtocolChannelPipelineFactory getChannelPipelineFactory();

	void setChannelPipelineFactory(ProtocolChannelPipelineFactory channelPipelineFactory);

	void addClientEventListener(ClientEventListener clientEventListener);

	void removeClientEventListener(ClientEventListener clientEventListener);

	SimpleChannelUpstreamHandler getChannelUpstreamHandler();

	boolean connect(String address, int port);

	boolean isConnected();

	ChannelFuture disconnect();

	ChannelFuture close();

	ChannelFuture sendMessage(Message message);

	Channel getChannel();

}
