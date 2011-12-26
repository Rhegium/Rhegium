package org.rhegium.api.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Message {

	/**
	 * Returns the {@link MessageType} of the actual {@link Message}
	 * implementation.
	 * 
	 * @return MessageType of this message
	 */
	MessageType getMessageType();

	/**
	 * Reads data for this message directly from the given {@link DataInput}.
	 * Inner handling of how to read the messages content relays on chosen
	 * implementation.
	 * 
	 * @param input
	 *            DataInput to read from
	 * @throws IOException
	 *             If any error needs to be handled while reading
	 */
	void read(DataInput input) throws IOException;

	/**
	 * Writes data for this message directly to the given {@link DataOutput}.
	 * Inner handling of how to write the messages content relays on chosen
	 * implementation.
	 * 
	 * @param output
	 *            DataOutput to read from
	 * @throws IOException
	 *             If any error needs to be handled while writing
	 */
	void write(DataOutput output) throws IOException;

}
