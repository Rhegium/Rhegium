package org.rhegium.api.network.protocol;

public interface PacketTypeInitializer {

	void initializePacketHandler(ProtocolMessageTypeHandler messageTypeHandler);

}
