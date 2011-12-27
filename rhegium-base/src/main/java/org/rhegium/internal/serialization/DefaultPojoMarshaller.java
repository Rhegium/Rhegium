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
package org.rhegium.internal.serialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rhegium.api.serialization.Attribute;
import org.rhegium.api.serialization.AttributeAccessorException;
import org.rhegium.api.serialization.IllegalOptionalAttributeException;
import org.rhegium.api.serialization.Marshaller;
import org.rhegium.api.serialization.Unmarshaller;
import org.rhegium.api.serialization.accessor.Accessor;
import org.rhegium.api.serialization.accessor.AccessorService;

import com.google.inject.Inject;

class DefaultPojoMarshaller<O> implements Marshaller<O>, Unmarshaller<O> {

	@Inject
	private AccessorService accessorService;

	private static final Map<Class<?>, Set<AttributeDescriptor>> ATTRIBUTE_CACHE = new HashMap<Class<?>, Set<AttributeDescriptor>>();
	private static final Comparator<AttributeDescriptor> ATTRIBUTE_COMPARATOR = new AttributeComparator();

	@Override
	public void unmarshal(O object, DataInput stream) throws IOException {
		final Collection<AttributeDescriptor> attributes = getAttributes(object.getClass());
		for (final AttributeDescriptor attribute : attributes) {
			writeAttributeValue(stream, attribute, object);
		}
	}

	@Override
	public void marshal(O object, DataOutput stream) throws IOException {
		final Collection<AttributeDescriptor> attributes = getAttributes(object.getClass());
		for (final AttributeDescriptor attribute : attributes) {
			readAttributeValue(stream, attribute, object);
		}
	}

	private static void quickCheckLegalOptionalAttribute(Collection<AttributeDescriptor> attributes) {
		int index = 0;
		final int length = attributes.size();
		for (final AttributeDescriptor attribute : attributes) {
			if (index < length - 1 && attribute.isOptional()) {
				throw new IllegalOptionalAttributeException(String.format(
						"Optional attributes can only be the last attribute in a packet: %s", attribute));
			}

			index++;
		}
	}

	private void writeAttributeValue(DataInput input, AttributeDescriptor attribute, Object object) throws IOException {
		final Class<?> type = attribute.getType();

		// Reserved for later use to preserve space in protocol data
		@SuppressWarnings("unused")
		final int length = attribute.getProtocolAttribute().length();

		final Accessor<?> accessor = accessorService.resolveAccessor(type);
		if (accessor == null) {
			throw new AttributeAccessorException(String.format("Accessor for attribute %s with type %s not found",
					attribute, type));
		}

		accessor.run(input, object, attribute, AccessorType.Get);
	}

	private void readAttributeValue(DataOutput output, AttributeDescriptor attribute, Object object) throws IOException {
		final Class<?> type = attribute.getType();

		// Reserved for later use to preserve space in protocol data
		@SuppressWarnings("unused")
		final int length = attribute.getProtocolAttribute().length();

		final Accessor<?> accessor = accessorService.resolveAccessor(type);
		if (accessor == null) {
			throw new AttributeAccessorException(String.format("Accessor for attribute %s with type %s not found",
					attribute, type));
		}

		accessor.run(output, object, attribute, AccessorType.Set);
	}

	private Collection<AttributeDescriptor> getAttributes(Class<?> clazz) {
		// Is class info cached?
		final Set<AttributeDescriptor> attributes = ATTRIBUTE_CACHE.get(clazz);
		if (attributes != null) {
			// Let's sort the attributes and return them
			final List<AttributeDescriptor> result = new ArrayList<AttributeDescriptor>(attributes);
			Collections.sort(result, ATTRIBUTE_COMPARATOR);
			return Collections.unmodifiableList(result);
		}

		// It isn't so just analyze class
		try {
			final Set<AttributeDescriptor> foundAttributes = new HashSet<AttributeDescriptor>();
			final Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Attribute.class)) {
					foundAttributes.add(new AttributeDescriptor(field.getAnnotation(Attribute.class), field,
							accessorService));
				}
			}

			// Cache value for next run - we do not need to synchronize this
			// since multiple runs result in same result
			ATTRIBUTE_CACHE.put(clazz, foundAttributes);

			// Let's sort the attributes and return them
			final List<AttributeDescriptor> result = new ArrayList<AttributeDescriptor>(foundAttributes);
			Collections.sort(result, ATTRIBUTE_COMPARATOR);

			// Test if an optional attribute is not at last position
			quickCheckLegalOptionalAttribute(foundAttributes);

			return Collections.unmodifiableList(result);

		}
		catch (Exception e) {
			throw new IllegalStateException("Message class could not be analysed", e);
		}
	}

}
