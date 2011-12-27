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
