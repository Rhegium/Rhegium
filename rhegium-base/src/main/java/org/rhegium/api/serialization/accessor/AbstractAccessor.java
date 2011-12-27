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
package org.rhegium.api.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import org.rhegium.api.serialization.AttributeAccessorException;
import org.rhegium.internal.serialization.AccessorType;
import org.rhegium.internal.serialization.AttributeDescriptor;
import org.rhegium.internal.serialization.accessor.FieldAccessorStrategyType;
import org.rhegium.internal.serialization.accessor.FieldPropertyAccessor;
import org.rhegium.internal.serialization.accessor.FieldPropertyAccessorFactory;

import com.google.inject.Inject;

public abstract class AbstractAccessor<T> implements Accessor<T> {

	@Inject
	private AccessorService accessorService;

	private FieldAccessorStrategyType fieldAccessorStrategyType = FieldAccessorStrategyType.Bytecode;

	@Override
	public void run(Object stream, Object object, AttributeDescriptor attribute, AccessorType accessorType)
			throws IOException {
		switch (accessorType) {
			case Set:
				handleSet(stream, object, attribute);
				break;

			case Get:
				handleGet(stream, object, attribute);
				break;
		}
	}

	@Override
	public int getSerializedSize() {
		return -1;
	}

	protected AccessorService getAccessorService() {
		return accessorService;
	}

	private void handleSet(Object stream, Object object, AttributeDescriptor attribute) throws IOException {
		T value = doGetProperty(object, attribute);

		// If value if null and attribute is non optional
		if (value == null && !attribute.isOptional()) {
			throw new AttributeAccessorException(String.format("Cannot serialize null for attribute %s", attribute));
		}

		// At this point attribute would be optional if value is null so just
		// skip serialization
		if (value != null) {
			writeValue((DataOutput) stream, value);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleGet(Object stream, Object object, AttributeDescriptor attribute) throws IOException {
		if (attribute.isOptional() && !(stream instanceof InputStream)) {
			throw new AttributeAccessorException(String.format("Cannot deserialize optional attribute %s"
					+ " since stream is no InputStream extending class", attribute));
		}
		final InputStream inputStream = (InputStream) stream;
		if (attribute.isOptional() && inputStream.available() < attribute.getSerializedSize(inputStream)) {
			// Just skip since stream buffer (of this packet) is smaller than
			// needed for attribute so optional element cannot be read
			return;
		}

		T result = readValue((DataInput) stream, (Class<T>) attribute.getType());
		doSetProperty(result, object, attribute);
	}

	public FieldAccessorStrategyType getFieldAccessorStrategyType() {
		return fieldAccessorStrategyType;
	}

	public void setFieldAccessorStrategyType(FieldAccessorStrategyType fieldAccessorStrategyType) {
		this.fieldAccessorStrategyType = fieldAccessorStrategyType;
	}

	private T doGetProperty(Object object, AttributeDescriptor attribute) {
		final FieldPropertyAccessor<T> propertyAccessor = FieldPropertyAccessorFactory.buildFieldPropertyAccessor(
				fieldAccessorStrategyType, attribute);

		return propertyAccessor.read(object, attribute);
	}

	private void doSetProperty(T value, Object object, AttributeDescriptor attribute) {
		final FieldPropertyAccessor<T> propertyAccessor = FieldPropertyAccessorFactory.buildFieldPropertyAccessor(
				fieldAccessorStrategyType, attribute);

		propertyAccessor.write(value, object, attribute);
	}

	public abstract void writeValue(DataOutput stream, T value) throws IOException;

	public abstract T readValue(DataInput stream, Class<?> type) throws IOException;

}
