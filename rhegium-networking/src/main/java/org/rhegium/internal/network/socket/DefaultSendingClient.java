package org.rhegium.internal.network.socket;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.socket.SendingClient;

public class DefaultSendingClient implements SendingClient {

	private final Channel channel;
	private final long messageId;

	public DefaultSendingClient(Channel channel, long messageId) {
		this.channel = channel;
		this.messageId = messageId;
	}

	@Override
	public ChannelFuture sendMessage(Message message) {
		if (message instanceof AbstractMessage) {
			((AbstractMessage) message).setMessageId(messageId);
		}

		return channel.write(message);
	}

}
