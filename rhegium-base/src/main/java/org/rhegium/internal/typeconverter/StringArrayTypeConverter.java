package org.rhegium.internal.typeconverter;

import org.rhegium.api.typeconverter.TypeConverter;

class StringArrayTypeConverter implements TypeConverter {

	@Override
	public boolean acceptType(Class<?> type) {
		return String[].class.isAssignableFrom(type);
	}

	@Override
	public String[] convert(final Object value, Class<?> type) {
		return value.toString().split(",");
	}
}
