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
