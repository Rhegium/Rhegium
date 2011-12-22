package org.rhegium.api.network;

public interface MessageTypeService {

	void registerMessageType(MessageType messageType);

	void removeMessageType(MessageType messageType);

	MessageType findByMessageTypeId(long messageTypeId);

}
