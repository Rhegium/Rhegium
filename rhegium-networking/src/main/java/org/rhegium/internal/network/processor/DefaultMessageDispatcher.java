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
package org.rhegium.internal.network.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.ClientEventListener;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.processor.MessageDispatcher;
import org.rhegium.api.network.processor.MessageFuture;
import org.rhegium.api.network.socket.NetworkingClient;

public class DefaultMessageDispatcher implements ClientEventListener, MessageDispatcher {

	private final Map<Long, MessageReceivedListener> listeners = new ConcurrentHashMap<Long, MessageReceivedListener>();

	private final ScheduledExecutorService executorService;

	private boolean timeTracingEnabled = false;

	public DefaultMessageDispatcher(ScheduledExecutorService executorService) {
		this.executorService = executorService;
	}

	@Override
	public void clientConnected() {
	}

	@Override
	public void clientClosed() {
	}

	@Override
	public void clientDisconnected() {
	}

	@Override
	public void exceptionCaught(Throwable throwable) throws Throwable {
	}

	@Override
	public void onMessage(final Message message) {
		if (message instanceof AbstractMessage) {
			AbstractMessage response = (AbstractMessage) message;

			final MessageReceivedListener listener = listeners.get(response.getMessageId());
			if (listener != null) {
				listeners.remove(response.getMessageId());
				executorService.submit(new Runnable() {

					@Override
					public void run() {
						listener.messageReceived(message);
					}
				});
			}
		}
	}

	@Override
	public void addMessageEvent(long messageId, MessageReceivedListener listener) {
		listeners.put(messageId, listener);
	}

	@Override
	public <K extends AbstractMessage, V extends Message> MessageFuture<K, V> createMessageProcessor(
			final NetworkingClient serviceClient) {

		return new DefaultMessageFuture<K, V>(serviceClient, this, executorService);
	}

	public boolean isTimeTracingEnabled() {
		return timeTracingEnabled;
	}

	public void setTimeTracingEnabled(boolean timeTracingEnabled) {
		this.timeTracingEnabled = timeTracingEnabled;
	}

}
