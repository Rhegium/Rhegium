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
package org.rhegium.internal.network.protocol;

import java.io.DataInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.protocol.MessageTypeDecoder;
import org.rhegium.api.network.protocol.ProtocolMessageTypeHandler;

public class DefaultFrameDecoder extends LengthFieldBasedFrameDecoder implements MessageTypeDecoder {

	private final ProtocolMessageTypeHandler messageTypeHandler;

	public DefaultFrameDecoder() {
		super(1048576, 0, 4, 0, 0);
		this.messageTypeHandler = new DefaultProtocolMessageTypeHandler();
	}

	public DefaultFrameDecoder(ProtocolMessageTypeHandler messageTypeHandler) {
		super(1048576, 0, 4, 0, 0);
		this.messageTypeHandler = messageTypeHandler;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		final ChannelBuffer frame = (ChannelBuffer) super.decode(ctx, channel, buffer);

		if (frame == null) {
			return null;
		}

		// Skip length field
		frame.skipBytes(4);

		final ChannelBufferInputStream stream = new ChannelBufferInputStream(frame);

		// Build message by using message type
		final Message message = messageTypeHandler.handlePacket(stream);

		// If message is null, dismiss the packet
		if (message == null) {
			frame.clear();
			return null;
		}

		// Read message
		message.read(new DataInputStream(stream));

		return message;
	}

	@Override
	protected ChannelBuffer extractFrame(ChannelBuffer buffer, int index, int length) {
		return buffer.slice(index, length);
	}

	@Override
	public ProtocolMessageTypeHandler getProtocolMessageTypeHandler() {
		return messageTypeHandler;
	}

}
