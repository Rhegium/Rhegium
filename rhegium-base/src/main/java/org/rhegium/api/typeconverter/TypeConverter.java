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
