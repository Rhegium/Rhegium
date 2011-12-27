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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
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

public class OriginalCommunicationInterfaceTestCase {

	private static final Calendar CALENDAR;
	private static final byte[] CAFEBABE;
	private static final int PING_VALUE = 3;

	static {
		CALENDAR = Calendar.getInstance();
		CALENDAR.set(Calendar.YEAR, 2000);
		CALENDAR.set(Calendar.MONTH, 1);
		CALENDAR.set(Calendar.DAY_OF_MONTH, 1);
		CALENDAR.set(Calendar.HOUR_OF_DAY, 12);
		CALENDAR.set(Calendar.MINUTE, 0);
		CALENDAR.set(Calendar.SECOND, 0);
		CALENDAR.set(Calendar.MILLISECOND, 0);

		CAFEBABE = new byte[4];
		CAFEBABE[0] = (byte) 0xCA;
		CAFEBABE[1] = (byte) 0xFE;
		CAFEBABE[2] = (byte) 0xBA;
		CAFEBABE[3] = (byte) 0xBE;
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
		ping.setPingValue(PING_VALUE);
		ping.setPingBytes(CAFEBABE);
		client.sendMessage(ping);

		semaphore.acquire(2);
		future.cancel(true);

		assertNotNull(pingMessage.get());
		assertNotNull(pongMessage.get());

		assertEquals(PING_VALUE, pingMessage.get().getPingValue());
		assertArrayEquals(CAFEBABE, pingMessage.get().getPingBytes());

		assertEquals(PING_VALUE, pongMessage.get().getPingValue());
		assertArrayEquals(CAFEBABE, pongMessage.get().getPingBytes());
		assertEquals(CALENDAR.getTimeInMillis(), pongMessage.get().getPongTimestamp().getTime());
		assertEquals(PongMessage.class.getCanonicalName(), pongMessage.get().getPongClassName());

		client.close();
		server.close();

		executor.shutdownNow();
	}

	@MessageTypeHandler(PongMessageHandler.class)
	public static class PongMessage extends AbstractMessage {

		@Attribute(index = 1)
		private int pingValue;

		@Attribute(index = 2)
		private byte[] pingBytes;

		@Attribute(index = 3)
		private Date pongTimestamp;

		@Attribute(index = 4)
		private String pongClassName;

		public PongMessage() {
			super(TestMessageType.Pong);
		}

		public int getPingValue() {
			return pingValue;
		}

		public void setPingValue(int pingValue) {
			this.pingValue = pingValue;
		}

		public byte[] getPingBytes() {
			return pingBytes;
		}

		public void setPingBytes(byte[] pingBytes) {
			this.pingBytes = pingBytes;
		}

		public Date getPongTimestamp() {
			return pongTimestamp;
		}

		public void setPongTimestamp(Date pongTimestamp) {
			this.pongTimestamp = pongTimestamp;
		}

		public String getPongClassName() {
			return pongClassName;
		}

		public void setPongClassName(String pongClassName) {
			this.pongClassName = pongClassName;
		}
	}

	@MessageTypeHandler(PingMessageHandler.class)
	public static class PingMessage extends AbstractMessage {

		@Attribute(index = 1)
		private int pingValue;

		@Attribute(index = 2)
		private byte[] pingBytes;

		public PingMessage() {
			super(TestMessageType.Ping);
		}

		public int getPingValue() {
			return pingValue;
		}

		public void setPingValue(int pingValue) {
			this.pingValue = pingValue;
		}

		public byte[] getPingBytes() {
			return pingBytes;
		}

		public void setPingBytes(byte[] pingBytes) {
			this.pingBytes = pingBytes;
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
			pong.setPingValue(message.getPingValue());
			pong.setPingBytes(message.getPingBytes());
			pong.setPongTimestamp(new Date(CALENDAR.getTimeInMillis()));
			pong.setPongClassName(PongMessage.class.getCanonicalName());

			client.sendMessage(pong);
		}
	}

}
