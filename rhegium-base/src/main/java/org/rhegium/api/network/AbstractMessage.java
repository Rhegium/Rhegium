package org.rhegium.api.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import de.heldenreich.wcc.commons.net.serializer.Marshaller;
import de.heldenreich.wcc.commons.net.serializer.MarshallerFactory;
import de.heldenreich.wcc.commons.net.serializer.Unmarshaller;

public abstract class AbstractMessage implements Message {

	private static final AtomicLong GLOBAL_MESSAGE_ID = new AtomicLong();

	private final MessageType messageType;

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
		Unmarshaller<Message> unmarshaller = MarshallerFactory.createUnmarshaller();
		unmarshaller.unmarshal(this, input);
	}

	protected void doWrite(DataOutput output) throws IOException {
		Marshaller<Message> marshaller = MarshallerFactory.createMarshaller();
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
