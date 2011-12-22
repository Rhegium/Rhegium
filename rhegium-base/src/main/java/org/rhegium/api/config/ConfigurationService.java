package org.rhegium.api.config;

import java.util.Collection;

public interface ConfigurationService {

	/**
	 * Retrieves a configuration value from the configuration store by using
	 * given {@link Configuration} value.
	 * 
	 * @param <V>
	 *            The value's type
	 * @param <T>
	 *            The enumeration type of the configuration
	 * @param configuration
	 *            Configuration key to search for
	 * @return The configuration value (or the default value if key was not
	 *         found)
	 */
	<T extends Enum<T> & Configuration<T>, V> V getProperty(T configuration);

	/**
	 * Retrieves a configuration value from the configuration store by using
	 * given {@link Configuration} value and configuration wildcard expression.
	 * 
	 * @param <V>
	 *            The value's type
	 * @param <T>
	 *            The enumeration type of the configuration
	 * @param configuration
	 *            Configuration key to search for
	 * @param expression
	 *            The configuration's wildcard expression to complete
	 *            configuration key
	 * @return The configuration value (or the default value if key was not
	 *         found)
	 */
	<T extends Enum<T> & Configuration<T>, V> V getProperty(T configuration, String expression);

	/**
	 * Retrieves a configuration value by using the given configuration key and
	 * configuration wildcard expression.
	 * 
	 * @param <V>
	 *            The value's type
	 * @param configurationKey
	 *            The configuration key to search for
	 * @param expression
	 *            The configuration's wildcard expression to complete
	 *            configuration key
	 * @param type
	 *            The class type of the value
	 * @return The configuration value (or null for objects and wrappers, false
	 *         for boolean and 0 for number types if key was not found)
	 */
	<V> V getProperty(String configurationKey, String expression, Class<V> type);

	/**
	 * Retrieves the raw String value from the underlying configuration store
	 * by using given key argument.
	 * 
	 * @param key
	 *            The key name to search for
	 * @return The raw property value as a String
	 */
	String getPropertyValue(String key);

	/**
	 * Returns a collection of all known configuration keys.
	 * 
	 * @return Collection of configuration keys
	 */
	Collection<String> getKeys();

}
