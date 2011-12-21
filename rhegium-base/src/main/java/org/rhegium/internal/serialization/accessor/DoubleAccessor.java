package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class DoubleAccessor extends AbstractAccessor<Double> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Double value) throws IOException {
		stream.writeDouble(value.doubleValue());
	}

	@Override
	public Double readValue(DataInput stream, Class<?> type) throws IOException {
		return Double.valueOf(stream.readDouble());
	}

	@Override
	public int getSerializedSize() {
		return 8;
	}

}
