package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class BooleanAccessor extends AbstractAccessor<Boolean> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Boolean value) throws IOException {
		stream.writeBoolean(value.booleanValue());
	}

	@Override
	public Boolean readValue(DataInput stream, Class<?> type) throws IOException {
		return Boolean.valueOf(stream.readBoolean());
	}

	@Override
	public int getSerializedSize() {
		return 1;
	}

}
