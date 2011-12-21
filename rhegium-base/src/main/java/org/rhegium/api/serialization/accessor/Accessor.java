package org.rhegium.api.serialization.accessor;

import java.io.IOException;

import org.rhegium.internal.serialization.AttributeDescriptor;

public interface Accessor<T> {

	boolean acceptType(Class<?> type);

	void run(Object stream, Object object, AttributeDescriptor attribute, AccessorType accessorType) throws IOException;

	int getSerializedSize();

}
