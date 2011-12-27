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
package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class PrimitiveByteArrayAccessor extends AbstractAccessor<byte[]> {

	@Override
	public boolean acceptType(Class<?> type) {
		return byte[].class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, byte[] value) throws IOException {
		final int length = value.length;

		stream.writeInt(length);
		stream.write(value);
	}

	@Override
	public byte[] readValue(DataInput stream, Class<?> type) throws IOException {
		final int length = stream.readInt();
		final byte[] data = new byte[length];

		stream.readFully(data);

		return data;
	}

}
