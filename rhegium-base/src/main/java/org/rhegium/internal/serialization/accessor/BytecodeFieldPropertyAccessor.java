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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.rhegium.internal.serialization.AttributeDescriptor;

import com.esotericsoftware.reflectasm.FieldAccess;

class BytecodeFieldPropertyAccessor<T> implements FieldPropertyAccessor<T> {

	private final Map<Class<?>, FieldAccess> FIELD_ACCESS_CACHE = new HashMap<Class<?>, FieldAccess>();
	private final Lock lock = new ReentrantLock();

	@Override
	@SuppressWarnings("unchecked")
	public T read(Object object, AttributeDescriptor attribute) {
		final FieldAccess fieldAccess = getFieldAccess(object.getClass());
		return (T) fieldAccess.get(object, attribute.getField().getName());
	}

	@Override
	public void write(T value, Object object, AttributeDescriptor attribute) {
		final FieldAccess fieldAccess = getFieldAccess(object.getClass());
		fieldAccess.set(object, attribute.getField().getName(), value);
	}

	private FieldAccess getFieldAccess(Class<?> clazz) {
		FieldAccess fieldAccess = null;
		try {
			lock.lock();
			fieldAccess = FIELD_ACCESS_CACHE.get(clazz);

		}
		finally {
			lock.unlock();
		}

		if (fieldAccess != null) {
			return fieldAccess;
		}

		fieldAccess = FieldAccess.get(clazz);

		try {
			lock.lock();
			FIELD_ACCESS_CACHE.put(clazz, fieldAccess);

		}
		finally {
			lock.unlock();
		}

		return fieldAccess;
	}

}
