package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class LongAccessor extends AbstractAccessor<Long> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Long value) throws IOException {
		stream.writeLong(value.longValue());
	}

	@Override
	public Long readValue(DataInput stream, Class<?> type) throws IOException {
		return Long.valueOf(stream.readLong());
	}

	@Override
	public int getSerializedSize() {
		return 8;
	}

}
