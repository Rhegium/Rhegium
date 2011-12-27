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

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.ClientEventListener;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.ProtocolChannelException;
import org.rhegium.api.network.protocol.MessageTypeDecoder;
import org.rhegium.api.network.protocol.PacketTypeInitializer;
import org.rhegium.api.network.protocol.ProtocolChannelPipelineFactory;
import org.rhegium.api.network.socket.NetworkingClient;
import org.rhegium.internal.network.protocol.DefaultProtocolChannelPipelineFactory;

class DefaultNetworkingClient implements NetworkingClient {

	private final SimpleChannelUpstreamHandler channelUpstreamHandler = new InnerEventHandler();
	private final Set<ClientEventListener> clientEventListeners = new HashSet<ClientEventListener>();
	private final Lock clientEventListenersLock = new ReentrantLock();

	private ProtocolChannelPipelineFactory channelPipelineFactory = new DefaultProtocolChannelPipelineFactory(
			channelUpstreamHandler);

	private final PacketTypeInitializer initializer;
	private final Executor executor;

	private Channel channel;

	DefaultNetworkingClient(Executor executor, final PacketTypeInitializer initializer) {
		this.executor = executor;
		this.initializer = initializer;
	}

	public ProtocolChannelPipelineFactory getChannelPipelineFactory() {
		return channelPipelineFactory;
	}

	public void setChannelPipelineFactory(ProtocolChannelPipelineFactory channelPipelineFactory) {
		this.channelPipelineFactory = channelPipelineFactory;
	}

	public void addClientEventListener(ClientEventListener clientEventListener) {
		try {
			clientEventListenersLock.lock();
			clientEventListeners.add(clientEventListener);
		}
		finally {
			clientEventListenersLock.unlock();
		}
	}

	public void removeClientEventListener(ClientEventListener clientEventListener) {
		try {
			clientEventListenersLock.lock();
			clientEventListeners.remove(clientEventListener);
		}
		finally {
			clientEventListenersLock.unlock();
		}
	}

	protected Set<ClientEventListener> getClientEventListeners() {
		return Collections.unmodifiableSet(clientEventListeners);
	}

	public SimpleChannelUpstreamHandler getChannelUpstreamHandler() {
		return channelUpstreamHandler;
	}

	public boolean connect(String address, int port) {
		// Initialize (register) the packet handlers
		MessageTypeDecoder decoder = channelPipelineFactory.getMessageTypeDecoder();
		if (decoder != null) {
			initializer.initializePacketHandler(decoder.getProtocolMessageTypeHandler());
		}

		final ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(executor, executor));
		bootstrap.setPipelineFactory(channelPipelineFactory);
		final ChannelFuture future = bootstrap.connect(new InetSocketAddress(address, port));
		final Channel channel = future.awaitUninterruptibly().getChannel();

		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return false;
		}

		return (this.channel = channel) == channel;
	}

	public boolean isConnected() {
		if (channel == null) {
			return false;
		}

		return channel.isConnected();
	}

	public ChannelFuture disconnect() {
		if (channel == null) {
			throw new IllegalStateException("Client is not connected");
		}

		return channel.disconnect();
	}

	public ChannelFuture close() {
		if (channel == null) {
			throw new IllegalStateException("Client is not connected");
		}

		return channel.close();
	}

	@Override
	public ChannelFuture sendMessage(Message message) {
		if (channel == null) {
			throw new IllegalStateException("Client is not connected");
		}

		return channel.write(message);
	}

	public Channel getChannel() {
		return channel;
	}

	private class InnerEventHandler extends SimpleChannelUpstreamHandler {

		@Override
		public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
			if (e.getMessage() instanceof Message) {
				long messageId = -1;
				if (e.getMessage() instanceof AbstractMessage) {
					messageId = ((AbstractMessage) e.getMessage()).getMessageId();
				}

				getChannelPipelineFactory().getMessageTypeDecoder().getProtocolMessageTypeHandler()
						.handleMessage((Message) e.getMessage(), new DefaultSendingClient(e.getChannel(), messageId));

				final ClientEventListener[] clientEventListeners = copyClientEventListeners();
				for (ClientEventListener listener : clientEventListeners) {
					listener.onMessage((Message) e.getMessage());
				}
			}

			super.messageReceived(ctx, e);
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			final ClientEventListener[] clientEventListeners = copyClientEventListeners();
			for (ClientEventListener listener : clientEventListeners) {
				listener.clientConnected();
			}

			super.channelConnected(ctx, e);
		}

		@Override
		public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			final ClientEventListener[] clientEventListeners = copyClientEventListeners();
			for (ClientEventListener listener : clientEventListeners) {
				listener.clientDisconnected();
			}

			super.channelDisconnected(ctx, e);
		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			final ClientEventListener[] clientEventListeners = copyClientEventListeners();
			for (ClientEventListener listener : clientEventListeners) {
				listener.clientClosed();
			}

			super.channelClosed(ctx, e);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			final ClientEventListener[] clientEventListeners = copyClientEventListeners();

			try {
				for (ClientEventListener listener : clientEventListeners) {
					listener.exceptionCaught(e.getCause());
				}
			}
			catch (Throwable t) {
				if (t instanceof Exception) {
					throw (Exception) t;
				}

				throw new ProtocolChannelException("Wrapped exception from channel exception handler", t);
			}
		}

		private ClientEventListener[] copyClientEventListeners() {
			final ClientEventListener[] clientEventListeners;
			try {
				clientEventListenersLock.lock();
				clientEventListeners = DefaultNetworkingClient.this.clientEventListeners
						.toArray(new ClientEventListener[0]);
			}
			finally {
				clientEventListenersLock.unlock();
			}

			return clientEventListeners;
		}
	}

}
