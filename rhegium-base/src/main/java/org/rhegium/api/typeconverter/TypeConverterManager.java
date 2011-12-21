package org.rhegium.api.typeconverter;

public interface TypeConverterManager {

	void registerTypeConverter(TypeConverter valueConverter);

	void removeTypeConverter(TypeConverter valueConverter);

	<T> T convert(Object value, Class<T> type);

}
