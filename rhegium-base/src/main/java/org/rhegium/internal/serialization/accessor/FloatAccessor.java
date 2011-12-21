package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class FloatAccessor extends AbstractAccessor<Float> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Float value) throws IOException {
		stream.writeFloat(value.floatValue());
	}

	@Override
	public Float readValue(DataInput stream, Class<?> type) throws IOException {
		return Float.valueOf(stream.readFloat());
	}

	@Override
	public int getSerializedSize() {
		return 4;
	}

}
