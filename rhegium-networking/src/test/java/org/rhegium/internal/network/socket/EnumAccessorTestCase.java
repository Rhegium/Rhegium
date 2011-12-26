package org.rhegium.internal.network.socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.MessageHandler;
import org.rhegium.api.network.MessageListener;
import org.rhegium.api.network.MessageType;
import org.rhegium.api.network.MessageTypeHandler;
import org.rhegium.api.network.protocol.PacketTypeInitializer;
import org.rhegium.api.network.protocol.ProtocolMessageTypeHandler;
import org.rhegium.api.network.socket.NetworkingClient;
import org.rhegium.api.network.socket.NetworkingServer;
import org.rhegium.api.network.socket.SendingClient;
import org.rhegium.api.serialization.Attribute;

public class EnumAccessorTestCase {

	public static enum TestEnum {
		Test1,
		Test2
	}

	@Test
	public void testCommunicationInterface() throws Exception {
		final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

		final Semaphore semaphore = new Semaphore(2);
		semaphore.acquire(2);

		final ScheduledFuture<Runnable> future = TestNetUtils.startTestTimoutExceeded(executor, new Runnable() {

			@Override
			public void run() {
				semaphore.release(2);
			}
		}, 5000);

		final int port = TestNetUtils.findFreePort();
		final AtomicReference<PingMessage> pingMessage = new AtomicReference<PingMessage>();
		final AtomicReference<PongMessage> pongMessage = new AtomicReference<PongMessage>();

		final NetworkingServer server = new DefaultNetworkingServer("127.0.0.1", port, executor, new PacketTypeInitializer() {

			@Override
			public void initializePacketHandler(ProtocolMessageTypeHandler messageTypeHandler) {
				messageTypeHandler.registerMessageHandler(PingMessage.class);
				messageTypeHandler.addMessageListener(new MessageListener() {

					@Override
					public void messageReceived(MessageType messageType, Message message, SendingClient client) {
						if (message instanceof PingMessage && messageType == TestMessageType.Ping) {
							pingMessage.set((PingMessage) message);
							semaphore.release();
						}
					}
				});
			}

		});

		final NetworkingClient client = new DefaultNetworkingClient(executor, new PacketTypeInitializer() {

			@Override
			public void initializePacketHandler(ProtocolMessageTypeHandler messageTypeHandler) {
				messageTypeHandler.registerMessageHandler(PongMessage.class, new PongMessageHandler(semaphore, pongMessage));
			}
		});

		server.bind();
		client.connect("127.0.0.1", port);

		final PingMessage ping = new PingMessage();
		ping.setKey(TestEnum.Test1);
		client.sendMessage(ping);

		semaphore.acquire(2);
		future.cancel(true);

		assertNotNull(pingMessage.get());
		assertNotNull(pongMessage.get());

		assertEquals(TestEnum.Test1, pingMessage.get().getKey());

		assertEquals(TestEnum.Test1, pongMessage.get().getKey());
		assertEquals(TestEnum.Test2, pongMessage.get().getVal());

		client.close();
		server.close();

		executor.shutdownNow();
	}

	@MessageTypeHandler(PongMessageHandler.class)
	public static class PongMessage extends AbstractMessage {

		@Attribute(index = 1)
		private TestEnum key;

		@Attribute(index = 2)
		private TestEnum val;

		public PongMessage() {
			super(TestMessageType.Ping);
		}

		public TestEnum getKey() {
			return key;
		}

		public void setKey(TestEnum key) {
			this.key = key;
		}

		public TestEnum getVal() {
			return val;
		}

		public void setVal(TestEnum val) {
			this.val = val;
		}
	}

	@MessageTypeHandler(PingMessageHandler.class)
	public static class PingMessage extends AbstractMessage {

		@Attribute(index = 1)
		private TestEnum key;

		public PingMessage() {
			super(TestMessageType.Ping);
		}

		public TestEnum getKey() {
			return key;
		}

		public void setKey(TestEnum key) {
			this.key = key;
		}
	}

	public static class PongMessageHandler implements MessageHandler<PongMessage> {

		private final Semaphore semaphore;
		private final AtomicReference<PongMessage> pongMessage;

		public PongMessageHandler(Semaphore semaphore, AtomicReference<PongMessage> pongMessage) {
			this.semaphore = semaphore;
			this.pongMessage = pongMessage;
		}

		@Override
		public void handleMessage(PongMessage message, SendingClient client) throws Exception {
			pongMessage.set(message);
			semaphore.release();
		}
	}

	public static class PingMessageHandler implements MessageHandler<PingMessage> {

		@Override
		public void handleMessage(PingMessage message, SendingClient client) throws Exception {
			final PongMessage pong = new PongMessage();
			pong.setKey(message.getKey());
			pong.setVal(TestEnum.Test2);

			client.sendMessage(pong);
		}
	}

}
