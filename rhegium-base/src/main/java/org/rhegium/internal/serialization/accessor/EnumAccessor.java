/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
