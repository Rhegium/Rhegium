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
