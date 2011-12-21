package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class ShortAccessor extends AbstractAccessor<Short> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Short value) throws IOException {
		stream.writeShort(value.shortValue());
	}

	@Override
	public Short readValue(DataInput stream, Class<?> type) throws IOException {
		return Short.valueOf(stream.readShort());
	}

	@Override
	public int getSerializedSize() {
		return 2;
	}

}
