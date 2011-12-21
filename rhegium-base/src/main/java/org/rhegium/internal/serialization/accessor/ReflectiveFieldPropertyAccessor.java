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
					throw new AttributeAccessorException("Could not read value from attribute " + attribute, e);
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
					throw new AttributeAccessorException("Could not write value to attribute " + attribute, e);
				}
			}
		});
	}

}
