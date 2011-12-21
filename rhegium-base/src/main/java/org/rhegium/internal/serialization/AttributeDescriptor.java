package org.rhegium.internal.serialization;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.rhegium.api.serialization.Attribute;
import org.rhegium.api.serialization.AttributeAccessorException;
import org.rhegium.api.serialization.accessor.AccessorService;

public final class AttributeDescriptor {

	private final AccessorService accessorService;

	private final Attribute attribute;
	private final boolean privateField;
	private final boolean optional;
	private final Field field;

	public AttributeDescriptor(Attribute attribute, Field field, AccessorService accessorService) {
		this.accessorService = accessorService;

		this.attribute = attribute;
		this.field = field;

		this.privateField = Modifier.isPrivate(field.getModifiers());
		this.optional = attribute.optional();

		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				AttributeDescriptor.this.field.setAccessible(true);
				return null;
			}
		});
	}

	public Attribute getProtocolAttribute() {
		return attribute;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return field.getType();
	}

	public boolean isPrivateField() {
		return privateField;
	}

	public boolean isOptional() {
		return optional;
	}

	public int getSerializedSize(InputStream stream) {
		// If attribute type is a string or a byte- / Byte-array we need to read
		// the size from stream without killing the real position
		if (String.class.isAssignableFrom(getType()) || byte[].class.isAssignableFrom(getType())
				|| Byte[].class.isAssignableFrom(getType())) {

			// If stream does not support position marking, we cannot go on at
			// this point so inform the user
			if (!stream.markSupported()) {
				throw new AttributeAccessorException("Cannot analyse stream for string or array size on attribute "
						+ this);
			}

			try {
				// Mark actual position inside stream
				// Actual 4 bytes should be enough for reading int length but...
				stream.mark(8);

				// Read Strings length
				int length = ((DataInput) stream).readInt();

				// Reset stream to meet deserializers expectations
				stream.reset();

				return length;
			}
			catch (IOException e) {
				return -1;
			}
		}

		return accessorService.getSerializedSize(getType());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeDescriptor other = (AttributeDescriptor) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		}
		else if (!field.equals(other.field))
			return false;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		}
		else if (!attribute.equals(other.attribute))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Attribute [attribute=" + attribute + ", field=" + field + "]";
	}

}
