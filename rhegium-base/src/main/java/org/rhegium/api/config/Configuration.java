package org.rhegium.api.config;

public interface Configuration<T> {

	Class<T> getConfigurationEnumType();

	String getKey();

	Class<?> getType();

	String getDefaultValue();

	boolean isMultiKey();

}
