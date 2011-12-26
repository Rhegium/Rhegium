package org.rhegium.internal.network.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;
import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.MessageHandler;
import org.rhegium.api.network.MessageListener;
import org.rhegium.api.network.protocol.ProtocolMessageTypeHandler;
import org.rhegium.api.network.socket.SendingClient;
import org.rhegium.api.serialization.Attribute;

public class ProtocolMarshallUnmarshalTestCase {

	@Test
	public void testMarshalUnmarshalBasic() throws Exception {
		final ProtocolMessageTypeHandler handler = new MarshallUnmarshallProtocolHandler();
		final MarshalUnmarshal writeInstance = new MarshalUnmarshal();

		final ChannelBuffer buffer = marshal(writeInstance);
		final MarshalUnmarshal readInstance = unmarshal(buffer, handler);

		assertNotNull(readInstance);
		assertEquals(writeInstance.level, readInstance.level);
		assertEquals(writeInstance.name, readInstance.name);
	}

	@Test
	public void testMarshalUnmarshalValuesSet() throws Exception {
		final ProtocolMessageTypeHandler handler = new MarshallUnmarshallProtocolHandler();
		final MarshalUnmarshal writeInstance = new MarshalUnmarshal();
		writeInstance.setLevel(10);
		writeInstance.setName("Farasande");

		final ChannelBuffer buffer = marshal(writeInstance);
		final MarshalUnmarshal readInstance = unmarshal(buffer, handler);

		assertNotNull(readInstance);
		assertEquals(writeInstance.level, readInstance.level);
		assertEquals(writeInstance.name, readInstance.name);
	}

	private ChannelBuffer marshal(Message message) throws IOException {
		final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		final DataOutput output = new ChannelBufferOutputStream(buffer);

		message.write(output);
		return buffer;
	}

	@SuppressWarnings("unchecked")
	private <T extends Message> T unmarshal(ChannelBuffer buffer, ProtocolMessageTypeHandler handler) throws Exception {
		final ChannelBufferInputStream input = new ChannelBufferInputStream(buffer);

		T message = (T) handler.handlePacket(input);
		message.read(input);
		return message;
	}

	public static class MarshalUnmarshal extends AbstractMessage {

		@Attribute(index = 1)
		private int level = 1;

		@Attribute(index = 2)
		private String name = "Alibert";

		public MarshalUnmarshal() {
			super(TestMessageType.TestRequest);
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class MarshallUnmarshallProtocolHandler implements ProtocolMessageTypeHandler {

		@Override
		public Message handlePacket(ChannelBufferInputStream stream) throws Exception {
			final long messageTypeId = stream.readLong();
			if (messageTypeId == TestMessageType.TestRequest.getMessageTypeId()) {
				return new MarshalUnmarshal();
			}

			return null;
		}

		@Override
		public void addMessageListener(MessageListener listener) {
		}

		@Override
		public void removeMessageListener(MessageListener listener) {
		}

		@Override
		public Collection<MessageListener> getMessageListeners() {
			return null;
		}

		@Override
		public <M extends Message> void registerMessageHandler(Class<M> messageClass) {
		}

		@Override
		public <M extends Message> void registerMessageHandler(Class<M> messageClass, MessageHandler<M> messageHandler) {
		}

		@Override
		public void handleMessage(Message message, SendingClient client) throws Exception {
		}
	}

}
