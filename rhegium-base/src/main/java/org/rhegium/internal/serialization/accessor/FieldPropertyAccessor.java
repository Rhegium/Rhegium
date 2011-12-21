package org.rhegium.internal.serialization.accessor;

import org.rhegium.internal.serialization.AttributeDescriptor;

public interface FieldPropertyAccessor<T> {

	T read(Object object, AttributeDescriptor attribute);

	void write(T value, Object object, AttributeDescriptor attribute);

}
