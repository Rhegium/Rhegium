package org.rhegium.api.network.protocol;

import org.jboss.netty.channel.ChannelPipelineFactory;

public interface ProtocolChannelPipelineFactory extends ChannelPipelineFactory {

	MessageTypeDecoder getMessageTypeDecoder();

}
