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
package org.rhegium.api.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.rhegium.api.serialization.Marshaller;
import org.rhegium.api.serialization.MarshallerService;
import org.rhegium.api.serialization.Unmarshaller;

import com.google.inject.Inject;

public abstract class AbstractMessage implements Message {

	private static final AtomicLong GLOBAL_MESSAGE_ID = new AtomicLong();

	private final MessageType messageType;

	@Inject
	private MarshallerService marshallerService;

	private long messageId = -1;

	public AbstractMessage(MessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public MessageType getMessageType() {
		return messageType;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	@Override
	public final void read(DataInput input) throws IOException {
		messageId = input.readLong();
		doRead(input);
	}

	@Override
	public final void write(DataOutput output) throws IOException {
		output.writeLong(messageType.getMessageTypeId());
		output.writeLong(messageId);
		doWrite(output);
	}

	protected void doRead(DataInput input) throws IOException {
		Unmarshaller<Message> unmarshaller = marshallerService.createUnmarshaller();
		unmarshaller.unmarshal(this, input);
	}

	protected void doWrite(DataOutput output) throws IOException {
		Marshaller<Message> marshaller = marshallerService.createMarshaller();
		marshaller.marshal(this, output);
	}

	public static <M extends AbstractMessage> M addRequestMessageId(M message) {
		message.setMessageId(GLOBAL_MESSAGE_ID.incrementAndGet());
		return message;
	}

	public static <M extends AbstractMessage> M copyRequestMessageId(M response, AbstractMessage request) {
		response.setMessageId(request.getMessageId());
		return response;
	}

}
