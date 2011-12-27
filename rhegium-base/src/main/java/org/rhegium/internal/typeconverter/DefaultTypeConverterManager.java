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

		throw new ConfigurationProvisionException(String.format("Value '%s' could not be converted", value));
	}

}
