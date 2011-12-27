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
package org.rhegium.api.network.processor;

import java.util.concurrent.TimeUnit;

import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.AsyncMessageListener;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.MessageListener;

public interface MessageFuture<K extends AbstractMessage, V extends Message> {

	/**
	 * Sends a message and waits for the response to be received. If the
	 * response cannot be retrieved in a timeout interval of 5 seconds a
	 * MessageAwaitTimeoutException is thrown.
	 * 
	 * @param message
	 *            Request message
	 * @return Response message
	 */
	V processMessage(K message);

	/**
	 * Sends a message and waits for the response to be received. If the
	 * response cannot be retrieved in a timeout interval of the given timeout
	 * value a MessageAwaitTimeoutException is thrown.
	 * 
	 * @param message
	 *            Request message
	 * @param timeout
	 *            The timeout value
	 * @param unit
	 *            The TimeUnit of the value
	 * @return Response message
	 */
	V processMessage(K message, int timeout, TimeUnit unit);

	/**
	 * Sends a message asynchronously. {@link MessageListener}.messageReceived
	 * is called if the response is received in a timeout of 5 seconds. If a
	 * response could not be retrieved nothing happens and the request is lost.
	 * 
	 * @param message
	 *            Request message
	 * @param messageListener
	 *            The message received listener
	 */
	void processMessageAsync(K message, MessageListener messageListener);

	/**
	 * Sends a message asynchronously. {@link MessageListener}.messageReceived
	 * is called if the response is received within the given timeout. If a
	 * response could not be retrieved nothing happens and the request is lost.
	 * 
	 * @param message
	 *            Request message
	 * @param messageListener
	 *            The message received listener
	 * @param timeout
	 *            The timeout value
	 * @param unit
	 *            The TimeUnit of the value
	 */
	void processMessageAsync(K message, MessageListener messageListener, int timeout, TimeUnit unit);

	/**
	 * Sends a message asynchronously. {@link AsyncMessageListener}
	 * .messageReceived
	 * is called if the response is received in a timeout of 5 seconds. If a
	 * response could not be retrieved or another exception happens then
	 * {@link AsyncMessageListener.messageFailure} is called.
	 * 
	 * @param message
	 *            Request message
	 * @param asyncMessageListener
	 *            The asynchronous message received listener
	 */
	void processMessageAsync(K message, AsyncMessageListener asyncMessageListener);

	/**
	 * Sends a message asynchronously. {@link AsyncMessageListener}
	 * .messageReceived
	 * is called if the response is received within the given timeout. If a
	 * response could not be retrieved or another exception happens then
	 * {@link AsyncMessageListener.messageFailure} is called.
	 * 
	 * @param message
	 *            Request message
	 * @param asyncMessageListener
	 *            The asynchronous message received listener
	 * @param timeout
	 *            The timeout value
	 * @param unit
	 *            The TimeUnit of the value
	 */
	void processMessageAsync(K message, AsyncMessageListener asyncMessageListener, int timeout, TimeUnit unit);

	/**
	 * Returns if the response time of the server is traced to console
	 * 
	 * @return If response time is traced
	 */
	boolean isTimeTracingEnabled();

	/**
	 * Sets if tracing should be enabled
	 * 
	 * @param tracing
	 *            Trace enabled
	 */
	void setTimeTracingEnabled(boolean tracing);

}
