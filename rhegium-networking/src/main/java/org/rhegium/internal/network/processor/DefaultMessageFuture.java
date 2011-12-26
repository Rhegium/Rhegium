package org.rhegium.internal.network.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.AsyncMessageListener;
import org.rhegium.api.network.FailureMessageRetrievedException;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.MessageListener;
import org.rhegium.api.network.processor.MessageAwaitTimeoutException;
import org.rhegium.api.network.processor.MessageDispatcher;
import org.rhegium.api.network.processor.MessageDispatcher.MessageReceivedListener;
import org.rhegium.api.network.processor.MessageFuture;
import org.rhegium.api.network.protocol.FailureMessage;
import org.rhegium.api.network.socket.NetworkingClient;
import org.rhegium.internal.network.socket.DefaultSendingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultMessageFuture<K extends AbstractMessage, V extends Message> implements MessageFuture<K, V> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageFuture.class);

	private final Semaphore semaphore = new Semaphore(1);
	private final AtomicReference<V> value = new AtomicReference<V>();
	private final AtomicReference<FailureMessage> failure = new AtomicReference<FailureMessage>();

	private final MessageDispatcher messageDispatcher;
	private final ExecutorService executorService;
	private final NetworkingClient serviceClient;

	private boolean timeTracingEnabled = false;

	DefaultMessageFuture(NetworkingClient serviceClient, MessageDispatcher messageDispatcher,
			ExecutorService executorService) {

		this.serviceClient = serviceClient;
		this.executorService = executorService;
		this.messageDispatcher = messageDispatcher;
	}

	@Override
	public V processMessage(final K message) {
		return processMessage(message, 5, TimeUnit.SECONDS);
	}

	@Override
	public V processMessage(final K message, final int timeout, final TimeUnit unit) {
		long time = System.currentTimeMillis();

		// Aquire permit to block before we prepare message
		semaphore.acquireUninterruptibly();

		// Add a unique message id to find response message
		AbstractMessage request = AbstractMessage.addRequestMessageId(message);

		// Async handling of incoming response messages
		messageDispatcher.addMessageEvent(request.getMessageId(), new MessageReceivedListener() {

			@Override
			@SuppressWarnings("unchecked")
			public void messageReceived(Message message) {
				if (message instanceof FailureMessage) {
					failure.set((FailureMessage) message);
				}
				else {
					value.set((V) message);
				}

				semaphore.release();
			}
		});

		// Send message
		serviceClient.sendMessage(request);

		// Wait for response to retrieved, waiting a
		try {
			if (semaphore.tryAcquire(timeout, unit)) {
				if (failure.get() != null) {
					throw new FailureMessageRetrievedException(failure.get());
				}

				return value.get();
			}
		}
		catch (InterruptedException e) {
			throw new MessageAwaitTimeoutException("Request call was interrupted", e);
		}
		finally {
			if (timeTracingEnabled) {
				long timeConsumed = System.currentTimeMillis() - time;
				LOG.info("Network for message " + message.getMessageId() + " needed " + timeConsumed + "ms");
			}
		}

		throw new MessageAwaitTimeoutException("Response of message " + message
				+ " could not be retrieved within defined timeout");
	}

	@Override
	public void processMessageAsync(final K message, final MessageListener messageListener) {
		processMessageAsync(message, messageListener, 5, TimeUnit.SECONDS);
	}

	@Override
	public void processMessageAsync(final K message, final MessageListener messageListener, final int timeout,
			final TimeUnit unit) {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				V response = processMessage(message, timeout, unit);
				messageListener.messageReceived(response.getMessageType(), response, new DefaultSendingClient(
						serviceClient.getChannel(), message.getMessageId()));
			}
		});
	}

	@Override
	public void processMessageAsync(final K message, final AsyncMessageListener asyncMessageListener) {
		processMessageAsync(message, asyncMessageListener, 5, TimeUnit.SECONDS);
	}

	@Override
	public void processMessageAsync(final K message, final AsyncMessageListener asyncMessageListener,
			final int timeout, final TimeUnit unit) {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					V response = processMessage(message, timeout, unit);
					asyncMessageListener.messageReceived(response.getMessageType(), response, new DefaultSendingClient(
							serviceClient.getChannel(), message.getMessageId()));
				}
				catch (Exception e) {
					asyncMessageListener.messageFailure(message, e, new DefaultSendingClient(
							serviceClient.getChannel(), message.getMessageId()));
				}
			}
		});
	}

	@Override
	public boolean isTimeTracingEnabled() {
		return timeTracingEnabled;
	}

	@Override
	public void setTimeTracingEnabled(final boolean tracing) {
		timeTracingEnabled = tracing;
	}

}
