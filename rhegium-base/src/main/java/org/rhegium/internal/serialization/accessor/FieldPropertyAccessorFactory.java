package org.rhegium.internal.serialization.accessor;

import org.rhegium.internal.serialization.AttributeDescriptor;

public class FieldPropertyAccessorFactory {

	private FieldPropertyAccessorFactory() {
	}

	public static <T> FieldPropertyAccessor<T> buildFieldPropertyAccessor(
			FieldAccessorStrategyType fieldAccessorStrategyType, AttributeDescriptor attribute) {

		if (attribute.isPrivateField()) {
			return new ReflectiveFieldPropertyAccessor<T>();
		}

		switch (fieldAccessorStrategyType) {
			case Reflection:
				return new ReflectiveFieldPropertyAccessor<T>();

			default:
				return new BytecodeFieldPropertyAccessor<T>();
		}
	}
}
