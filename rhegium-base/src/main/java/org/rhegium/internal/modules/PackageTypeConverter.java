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
