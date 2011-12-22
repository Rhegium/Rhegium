package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.objectweb.asm.Type;
import org.rhegium.api.serialization.AttributeAccessorException;
import org.rhegium.api.serialization.accessor.AbstractAccessor;

class EnumAccessor extends AbstractAccessor<Enum<?>> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Enum.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Enum<?> value) throws IOException {
		AbstractAccessor<Object> stringAccessor = (AbstractAccessor<Object>) getAccessorService().resolveAccessor(
				String.class);

		stringAccessor.writeValue(stream, Type.getInternalName(value.getClass()).replace("/", "."));
		stream.writeInt(value.ordinal());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Enum<?> readValue(DataInput stream, Class<?> type) throws IOException {
		AbstractAccessor<Object> stringAccessor = (AbstractAccessor<Object>) getAccessorService().resolveAccessor(
				String.class);

		try {
			String className = (String) stringAccessor.readValue(stream, String.class);
			Class<Enum<?>> clazz = (Class<Enum<?>>) Class.forName(className);
			Enum<?>[] values = clazz.getEnumConstants();

			// Class is no enum type
			if (values == null) {
				throw new AttributeAccessorException(String.format("Read class %s is no Enum type",
						clazz.getCanonicalName()));
			}

			int ordinal = stream.readInt();
			for (Enum<?> value : values) {
				if (value.ordinal() == ordinal) {
					return value;
				}
			}

			return null;
		}
		catch (Exception e) {
			throw new AttributeAccessorException(e);
		}
	}

}
