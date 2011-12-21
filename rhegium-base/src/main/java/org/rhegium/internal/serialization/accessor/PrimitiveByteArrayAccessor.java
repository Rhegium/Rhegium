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
