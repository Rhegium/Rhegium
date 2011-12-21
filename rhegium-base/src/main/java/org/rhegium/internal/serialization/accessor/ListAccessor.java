package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rhegium.api.serialization.AttributeAccessorException;
import org.rhegium.api.serialization.accessor.AbstractAccessor;

class ListAccessor extends AbstractAccessor<List<?>> {

	@Override
	public boolean acceptType(Class<?> type) {
		return List.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, List<?> value) throws IOException {
		stream.writeInt(value.size());

		if (value.size() > 0) {
			Object v = value.get(0);
			AbstractAccessor<Object> accessor = (AbstractAccessor<Object>) getAccessorService().resolveAccessor(
					v.getClass());

			AbstractAccessor<Object> stringAccessor = (AbstractAccessor<Object>) getAccessorService().resolveAccessor(
					String.class);

			stringAccessor.writeValue(stream, v.getClass().getCanonicalName());
			for (Object export : value) {
				accessor.writeValue(stream, export);
			}
		}
	}

	@Override
	public List<?> readValue(DataInput stream, Class<?> type) throws IOException {
		final List<Object> list = new ArrayList<Object>();

		AbstractAccessor<Object> stringAccessor = (AbstractAccessor<Object>) getAccessorService().resolveAccessor(
				String.class);

		final int size = stream.readInt();
		final String className = (String) stringAccessor.readValue(stream, String.class);
		try {
			final Class<?> clazz = Class.forName(className);
			AbstractAccessor<Object> accessor = (AbstractAccessor<Object>) getAccessorService().resolveAccessor(clazz);

			for (int i = 0; i < size; i++) {
				Object value = accessor.readValue(stream, clazz);
				list.add(value);
			}

			return list;
		}
		catch (Exception e) {
			throw new AttributeAccessorException(className, e);
		}
	}

}
