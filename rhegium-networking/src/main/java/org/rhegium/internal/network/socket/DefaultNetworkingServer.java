package org.rhegium.internal.network.socket;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.RemoteClientListener;
import org.rhegium.api.network.protocol.MessageTypeDecoder;
import org.rhegium.api.network.protocol.PacketTypeInitializer;
import org.rhegium.api.network.protocol.ProtocolChannelPipelineFactory;
import org.rhegium.api.network.socket.NetworkingServer;
import org.rhegium.internal.network.protocol.DefaultProtocolChannelPipelineFactory;

class DefaultNetworkingServer implements NetworkingServer {

	private final SimpleChannelUpstreamHandler channelUpstreamHandler = new InnerEventHandler();
	private final Set<RemoteClientListener> remoteClientListeners = new HashSet<RemoteClientListener>();
	private final Lock remoteClientListenersLock = new ReentrantLock();

	private final Set<DefaultRemoteNetworkingClient> remoteClients = new CopyOnWriteArraySet<DefaultRemoteNetworkingClient>();
	private final Lock remoteClientsLock = new ReentrantLock();

	private final PacketTypeInitializer initializer;
	private final Executor executor;
	private final String address;
	private final int port;

	private ProtocolChannelPipelineFactory channelPipelineFactory = new DefaultProtocolChannelPipelineFactory(
			channelUpstreamHandler);

	private ServerBootstrap serverBootstrap;
	private Channel serverChannel;

	DefaultNetworkingServer(final String address, final int port, final Executor executor,
			final PacketTypeInitializer initializer) {

		this.executor = executor;
		this.address = address;
		this.port = port;
		this.initializer = initializer;
	}

	public void bind() {
		// Initialize (register) the packet handlers
		MessageTypeDecoder decoder = channelPipelineFactory.getMessageTypeDecoder();
		if (decoder != null) {
			initializer.initializePacketHandler(decoder.getProtocolMessageTypeHandler());
		}

		serverBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(executor, executor));
		serverBootstrap.setPipelineFactory(channelPipelineFactory);
		serverChannel = serverBootstrap.bind(new InetSocketAddress(address, port));
	}

	public ChannelFuture close() {
		return serverChannel.close();
	}

	public ProtocolChannelPipelineFactory getChannelPipelineFactory() {
		return channelPipelineFactory;
	}

	public void setChannelPipelineFactory(ProtocolChannelPipelineFactory channelPipelineFactory) {
		this.channelPipelineFactory = channelPipelineFactory;
	}

	public void addRemoteClientListener(RemoteClientListener remoteClientListener) {
		try {
			remoteClientListenersLock.lock();
			remoteClientListeners.add(remoteClientListener);
		}
		finally {
			remoteClientListenersLock.unlock();
		}
	}

	public void removeRemoteClientListener(RemoteClientListener remoteClientListener) {
		try {
			remoteClientListenersLock.lock();
			remoteClientListeners.remove(remoteClientListener);
		}
		finally {
			remoteClientListenersLock.unlock();
		}
	}

	public SimpleChannelUpstreamHandler getChannelUpstreamHandler() {
		return channelUpstreamHandler;
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
			}

			super.messageReceived(ctx, e);
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			final RemoteClientListener[] remoteClientListeners = copyRemoteClientListeners();
			final DefaultRemoteNetworkingClient remoteClient = new DefaultRemoteNetworkingClient(e.getChannel());
			addRemoveClient(remoteClient);

			for (RemoteClientListener listener : remoteClientListeners) {
				listener.clientConnected(remoteClient);
			}

			super.channelConnected(ctx, e);
		}

		@Override
		public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			final RemoteClientListener[] remoteClientListeners = copyRemoteClientListeners();
			final DefaultRemoteNetworkingClient remoteClient = removeRemoveClient(e.getChannel());
			for (RemoteClientListener listener : remoteClientListeners) {
				listener.clientDisconnected(remoteClient);
			}

			super.channelDisconnected(ctx, e);
		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			final RemoteClientListener[] remoteClientListeners = copyRemoteClientListeners();
			final DefaultRemoteNetworkingClient remoteClient = removeRemoveClient(e.getChannel());
			for (RemoteClientListener listener : remoteClientListeners) {
				listener.clientClosed(remoteClient);
			}

			super.channelClosed(ctx, e);
		}

		private RemoteClientListener[] copyRemoteClientListeners() {
			final RemoteClientListener[] remoteClientListeners;
			try {
				remoteClientListenersLock.lock();

				remoteClientListeners = DefaultNetworkingServer.this.remoteClientListeners
						.toArray(new RemoteClientListener[0]);
			}
			finally {
				remoteClientListenersLock.unlock();
			}

			return remoteClientListeners;
		}

		private DefaultRemoteNetworkingClient findRemoteClient(Channel channel) {
			try {
				remoteClientsLock.lock();
				for (DefaultRemoteNetworkingClient remoteClient : remoteClients) {
					if (remoteClient.getChannel().equals(channel)) {
						return remoteClient;
					}
				}

				return null;
			}
			finally {
				remoteClientsLock.unlock();
			}
		}

		private DefaultRemoteNetworkingClient addRemoveClient(DefaultRemoteNetworkingClient remoteClient) {
			try {
				remoteClientsLock.lock();
				remoteClients.add(remoteClient);
				return remoteClient;
			}
			finally {
				remoteClientsLock.unlock();
			}
		}

		private DefaultRemoteNetworkingClient removeRemoveClient(Channel channel) {
			try {
				remoteClientsLock.lock();
				DefaultRemoteNetworkingClient remoteClient = findRemoteClient(channel);
				if (remoteClient != null) {
					remoteClients.add(remoteClient);
				}

				return remoteClient;
			}
			finally {
				remoteClientsLock.unlock();
			}
		}

	}

}
