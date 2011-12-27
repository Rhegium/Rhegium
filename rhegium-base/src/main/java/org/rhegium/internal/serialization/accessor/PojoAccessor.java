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
import java.lang.reflect.Field;

import org.rhegium.api.serialization.Attribute;
import org.rhegium.api.serialization.AttributeAccessorException;
import org.rhegium.api.serialization.Marshaller;
import org.rhegium.api.serialization.MarshallerService;
import org.rhegium.api.serialization.Unmarshaller;
import org.rhegium.api.serialization.accessor.AbstractAccessor;

import com.google.inject.Inject;

class PojoAccessor extends AbstractAccessor<Object> {

	@Inject
	private MarshallerService marshallerService;

	@Override
	public boolean acceptType(Class<?> type) {
		if (type.isPrimitive() || type.isArray() || type.isAnnotation() || type.isEnum() || type.isInterface()
				|| type.isSynthetic()) {
			return false;
		}

		for (Field field : type.getDeclaredFields()) {
			if (field.isAnnotationPresent(Attribute.class)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void writeValue(DataOutput stream, Object value) throws IOException {
		Marshaller<Object> marshaller = marshallerService.createMarshaller();
		marshaller.marshal(value, stream);
	}

	@Override
	public Object readValue(DataInput stream, Class<?> type) throws IOException {
		try {
			Object value = type.newInstance();

			Unmarshaller<Object> unmarshaller = marshallerService.createUnmarshaller();
			unmarshaller.unmarshal(value, stream);

			return value;
		}
		catch (Exception e) {
			throw new AttributeAccessorException("Attribute could not be read");
		}
	}

}
