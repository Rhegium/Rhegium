package org.rhegium.api.network;

@SuppressWarnings("serial")
public class ProtocolConfigurationException extends RuntimeException {

	public ProtocolConfigurationException() {
		super();
	}

	public ProtocolConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolConfigurationException(String message) {
		super(message);
	}

	public ProtocolConfigurationException(Throwable cause) {
		super(cause);
	}

}
