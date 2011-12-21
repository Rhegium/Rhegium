package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class DateAccessor extends AbstractAccessor<Date> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Date.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Date value) throws IOException {
		stream.writeLong(value.getTime());
	}

	@Override
	public Date readValue(DataInput stream, Class<?> type) throws IOException {
		final long value = stream.readLong();

		if (type.equals(java.sql.Timestamp.class)) {
			return new java.sql.Timestamp(value);
		}
		else if (type.equals(java.sql.Date.class)) {
			return new java.sql.Date(value);
		}

		return new Date(value);
	}

}
