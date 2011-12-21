package org.rhegium.api.config;

@SuppressWarnings("serial")
public class ConfigurationProvisionException extends RuntimeException {

	public ConfigurationProvisionException() {
		super();
	}

	public ConfigurationProvisionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationProvisionException(String message) {
		super(message);
	}

	public ConfigurationProvisionException(Throwable cause) {
		super(cause);
	}

}
