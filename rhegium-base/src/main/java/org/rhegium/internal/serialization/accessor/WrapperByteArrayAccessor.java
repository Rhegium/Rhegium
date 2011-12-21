package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class WrapperByteArrayAccessor extends AbstractAccessor<Byte[]> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Byte[].class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Byte[] value) throws IOException {
		final int length = value.length;
		final byte[] data = new byte[length];

		for (int i = 0; i < length; i++) {
			data[i] = value[i].byteValue();
		}

		stream.writeInt(length);
		stream.write(data);
	}

	@Override
	public Byte[] readValue(DataInput stream, Class<?> type) throws IOException {
		final int length = stream.readInt();
		final byte[] data = new byte[length];
		final Byte[] result = new Byte[length];

		stream.readFully(data);

		for (int i = 0; i < length; i++) {
			result[i] = Byte.valueOf(data[i]);
		}

		return result;
	}
}
