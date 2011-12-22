package org.rhegium.internal.network.protocol;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.rhegium.api.network.Message;

public class DefaultFrameEncoder extends LengthFieldPrepender {

	public DefaultFrameEncoder() {
		// Auto prepend message length to packet
		super(4);
	}

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		if (!(msg instanceof Message)) {
			return null;
		}

		final ChannelBuffer buffer = dynamicBuffer();
		final ChannelBufferOutputStream stream = new ChannelBufferOutputStream(buffer);

		// Write message data to packet
		((Message) msg).write(stream);

		return super.encode(ctx, channel, buffer);
	}

}
