package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class ByteAccessor extends AbstractAccessor<Byte> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Byte value) throws IOException {
		stream.writeByte(value.byteValue());
	}

	@Override
	public Byte readValue(DataInput stream, Class<?> type) throws IOException {
		return Byte.valueOf(stream.readByte());
	}

	@Override
	public int getSerializedSize() {
		return 1;
	}

}
