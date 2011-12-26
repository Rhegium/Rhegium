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
