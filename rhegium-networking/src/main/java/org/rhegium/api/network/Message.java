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
