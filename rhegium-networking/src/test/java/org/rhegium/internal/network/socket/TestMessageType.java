package org.rhegium.internal.network.socket;

import org.rhegium.api.network.MessageType;

public enum TestMessageType implements MessageType {
	TestRequest(1),
	TestResponse(2),

	Ping(3),
	Pong(4),

	;

	private final long messageType;

	private TestMessageType(long messageType) {
		this.messageType = messageType;
	}

	@Override
	public long getMessageTypeId() {
		return messageType;
	}

}
