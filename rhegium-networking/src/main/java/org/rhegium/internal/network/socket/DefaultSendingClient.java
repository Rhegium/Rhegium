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