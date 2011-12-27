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
