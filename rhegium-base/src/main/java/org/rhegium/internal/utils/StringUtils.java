package org.rhegium.internal.utils;

public final class StringUtils {

	private StringUtils() {
	}

	public static boolean isEmpty(String value) {
		if (value == null) {
			return true;
		}

		return value.isEmpty();
	}

	public static String join(String separator, String... strings) {
		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < strings.length; i++) {
			builder.append(strings[i]);

			if (i < strings.length - 2) {
				builder.append(separator);
			}
		}

		return builder.toString();
	}
}
