package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class StringAccessor extends AbstractAccessor<String> {

	@Override
	public boolean acceptType(Class<?> type) {
		return String.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, String value) throws IOException {
		final byte[] data = value.getBytes("UTF-8");
		final int length = data.length;
		stream.writeInt(length);
		stream.write(data);
	}

	@Override
	public String readValue(DataInput stream, Class<?> type) throws IOException {
		final int length = stream.readInt();
		final byte[] data = new byte[length];

		stream.readFully(data);
		return new String(data, Charset.forName("UTF-8"));
	}

}
