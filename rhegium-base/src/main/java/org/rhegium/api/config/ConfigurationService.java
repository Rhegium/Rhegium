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
	 * @return The configurations value (or the default value if key was not
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
	 * @return The configurations value (or the default value if key was not
	 *         found)
	 */
	<T extends Enum<T> & Configuration<T>, V> V getProperty(T configuration, String expression);

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
