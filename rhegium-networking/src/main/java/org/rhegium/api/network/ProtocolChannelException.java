package org.rhegium.api.network;

@SuppressWarnings("serial")
public class ProtocolChannelException extends RuntimeException {

	public ProtocolChannelException() {
		super();
	}

	public ProtocolChannelException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolChannelException(String message) {
		super(message);
	}

	public ProtocolChannelException(Throwable cause) {
		super(cause);
	}

}
