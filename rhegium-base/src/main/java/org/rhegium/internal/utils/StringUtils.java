package org.rhegium.internal.utils;

public final class StringUtils {

	private StringUtils() {
	}

	public static boolean isEmpty(final String value) {
		if (value == null) {
			return true;
		}

		return value.isEmpty();
	}

	public static String concat(final String... strings) {
		final StringBuilder builder = new StringBuilder();

		for (final String string : strings) {
			builder.append(string);
		}

		return builder.toString();
	}

}
