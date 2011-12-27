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
package org.rhegium.api.network.protocol;

import java.util.Collection;

import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.rhegium.api.network.Message;
import org.rhegium.api.network.MessageHandler;
import org.rhegium.api.network.MessageListener;
import org.rhegium.api.network.socket.SendingClient;

public interface ProtocolMessageTypeHandler {

	<M extends Message> void registerMessageHandler(Class<M> messageClass);

	<M extends Message> void registerMessageHandler(Class<M> messageClass, MessageHandler<M> messageHandler);

	void addMessageListener(MessageListener listener);

	void removeMessageListener(MessageListener listener);

	Collection<MessageListener> getMessageListeners();

	Message handlePacket(ChannelBufferInputStream stream) throws Exception;

	void handleMessage(Message message, SendingClient client) throws Exception;

}
