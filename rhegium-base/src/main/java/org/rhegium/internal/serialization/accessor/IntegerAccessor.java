package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class IntegerAccessor extends AbstractAccessor<Integer> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Integer value) throws IOException {
		stream.writeInt(value.intValue());
	}

	@Override
	public Integer readValue(DataInput stream, Class<?> type) throws IOException {
		return Integer.valueOf(stream.readInt());
	}

	@Override
	public int getSerializedSize() {
		return 4;
	}

}
