package org.rhegium.api.network.socket;

import org.jboss.netty.channel.ChannelFuture;
import org.rhegium.api.network.Message;

public interface SendingClient {

	ChannelFuture sendMessage(Message message);

}
