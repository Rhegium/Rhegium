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
