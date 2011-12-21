package org.rhegium.internal.typeconverter;

import org.rhegium.api.typeconverter.TypeConverter;

class BaseTypeConverter implements TypeConverter {

	@Override
	public boolean acceptType(Class<?> type) {
		return (type.equals(String.class) || type.equals(String[].class) || type.equals(byte[].class)
				|| type.equals(Byte[].class) || type.equals(boolean.class) || type.equals(Boolean.class)
				|| type.equals(byte.class) || type.equals(Byte.class) || type.equals(short.class)
				|| type.equals(Short.class) || type.equals(int.class) || type.equals(Integer.class)
				|| type.equals(long.class) || type.equals(Long.class) || type.equals(float.class)
				|| type.equals(Float.class) || type.equals(double.class) || type.equals(Double.class)
				|| type.equals(char[].class) || type.equals(Character[].class));
	}

	@Override
	public Object convert(Object value, Class<?> type) {
		if (type.equals(String.class)) {
			return value;
		}

		if (type.equals(byte[].class) || type.equals(Byte[].class)) {
			byte[] data;
			if (value.getClass().equals(byte[].class)) {
				data = (byte[]) value;

			}
			else {
				data = ((String) value).getBytes();
			}

			if (type.equals(byte[].class))
				return data;

			Byte[] array = new Byte[data.length];
			for (int i = 0; i < data.length; i++) {
				array[i] = Byte.valueOf(data[i]);
			}

			return array;
		}

		if (type.equals(String[].class)) {
			return ((String) value).split(",");
		}

		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return Boolean.parseBoolean((String) value);
		}

		if (type.equals(byte.class) || type.equals(Byte.class)) {
			return Byte.parseByte((String) value);
		}

		if (type.equals(short.class) || type.equals(Short.class)) {
			return Short.parseShort((String) value);
		}

		if (type.equals(int.class) || type.equals(Integer.class)) {
			return Integer.parseInt((String) value);
		}

		if (type.equals(long.class) || type.equals(Long.class)) {
			return Long.parseLong((String) value);
		}

		if (type.equals(float.class) || type.equals(Float.class)) {
			return Float.parseFloat((String) value);
		}

		if (type.equals(double.class) || type.equals(Double.class)) {
			return Double.parseDouble((String) value);
		}

		if (type.equals(char[].class) || type.equals(Character[].class)) {
			char[] data = ((String) value).toCharArray();

			if (type.equals(char[].class))
				return data;

			Character[] array = new Character[data.length];
			for (int i = 0; i < data.length; i++) {
				array[i] = Character.valueOf(data[i]);
			}

			return array;
		}

		return null;
	}

}
