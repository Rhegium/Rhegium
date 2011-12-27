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
package org.rhegium.internal.modules;

import org.sourceprojects.lycia.LyciaParser;
import org.sourceprojects.lycia.TypeConverter;
import org.w3c.dom.Element;

public class PackageTypeConverter implements TypeConverter {

	@Override
	public boolean accept(final Class<?> type) {
		return String.class.isAssignableFrom(type);
	}

	@Override
	public <B> Object convert(final Object value, final Class<?> type,
			final LyciaParser<B> parser) throws Exception {

		return convert(value, type);
	}

	@Override
	public Object convert(final Object value, final Class<?> type)
			throws Exception {

		if (!(value instanceof Element)) {
			return null;
		}

		return ((Element) value).getTextContent();
	}

}
