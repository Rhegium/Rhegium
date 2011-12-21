package org.rhegium.internal.typeconverter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.rhegium.api.config.ConfigurationProvisionException;
import org.rhegium.api.typeconverter.TypeConverter;
import org.rhegium.api.typeconverter.TypeConverterManager;

import com.google.inject.Inject;

class DefaultTypeConverterManager implements TypeConverterManager {

	private final Set<TypeConverter> typeConverters = new HashSet<TypeConverter>();
	private final ReentrantLock lock = new ReentrantLock();

	@Inject
	DefaultTypeConverterManager(Set<TypeConverter> typeConverters) {
		this.typeConverters.addAll(typeConverters);
	}

	@Override
	public void registerTypeConverter(TypeConverter valueConverter) {
		try {
			lock.lock();
			typeConverters.add(valueConverter);

		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public void removeTypeConverter(TypeConverter valueConverter) {
		try {
			lock.lock();
			typeConverters.remove(valueConverter);

		}
		finally {
			lock.unlock();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(Object value, Class<T> type) {
		final TypeConverter[] converters;
		try {
			lock.lock();
			converters = typeConverters.toArray(new TypeConverter[typeConverters.size()]);

		}
		finally {
			lock.unlock();
		}

		for (final TypeConverter valueConverter : converters) {
			if (valueConverter.acceptType(type)) {
				return (T) valueConverter.convert(value, type);
			}
		}

		throw new ConfigurationProvisionException("Value '" + value + "' could not be converted");
	}

}
