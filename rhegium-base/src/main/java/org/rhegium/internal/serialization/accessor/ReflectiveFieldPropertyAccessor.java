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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.rhegium.api.serialization.AttributeAccessorException;
import org.rhegium.internal.serialization.AttributeDescriptor;

class ReflectiveFieldPropertyAccessor<T> implements FieldPropertyAccessor<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T read(final Object object, final AttributeDescriptor attribute) {
		return AccessController.doPrivileged(new PrivilegedAction<T>() {

			@Override
			public T run() {
				try {
					return (T) attribute.getField().get(object);
				}
				catch (Exception e) {
					throw new AttributeAccessorException(String.format("Could not read value from attribute %s",
							attribute), e);
				}
			}
		});
	}

	@Override
	public void write(final T value, final Object object, final AttributeDescriptor attribute) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				try {
					attribute.getField().set(object, value);
					return null;
				}
				catch (Exception e) {
					throw new AttributeAccessorException(String.format("Could not write value to attribute %s",
							attribute), e);
				}
			}
		});
	}

}
