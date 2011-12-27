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
package org.rhegium.api.typeconverter;

public interface TypeConverter {

	/**
	 * Checks if the actual {@link TypeConverter} can handle the given type.
	 * 
	 * @param type
	 *            Type to check
	 * @return True if type can be handled otherwise false
	 */
	boolean acceptType(Class<?> type);

	/**
	 * Converts the given string value to a value of the previously requested
	 * type.
	 * 
	 * @param value
	 *            The value to be converted
	 * @param type
	 *            The type to be converted to
	 * @return The converted value
	 */
	Object convert(Object value, Class<?> type);

}
