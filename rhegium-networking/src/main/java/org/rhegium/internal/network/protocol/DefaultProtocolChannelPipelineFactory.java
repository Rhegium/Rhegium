package org.rhegium.internal.network.protocol;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.rhegium.api.network.protocol.MessageTypeDecoder;
import org.rhegium.api.network.protocol.ProtocolChannelPipelineFactory;

public class DefaultProtocolChannelPipelineFactory implements ProtocolChannelPipelineFactory {

	private final SimpleChannelUpstreamHandler channelUpstreamHandler;
	private final DefaultFrameDecoder decoder = new DefaultFrameDecoder();

	public DefaultProtocolChannelPipelineFactory() {
		this(null);
	}

	public DefaultProtocolChannelPipelineFactory(SimpleChannelUpstreamHandler channelUpstreamHandler) {
		this.channelUpstreamHandler = channelUpstreamHandler;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {

		final ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("encoder", new DefaultFrameEncoder());
		pipeline.addLast("decoder", decoder);

		if (channelUpstreamHandler != null) {
			pipeline.addLast("handler", channelUpstreamHandler);
		}

		return pipeline;
	}

	@Override
	public MessageTypeDecoder getMessageTypeDecoder() {
		return decoder;
	}

}
