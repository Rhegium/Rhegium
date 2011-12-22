package org.rhegium.api.network.protocol;

import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.MessageType;
import org.rhegium.api.serialization.Attribute;

public class FailureMessage extends AbstractMessage {

	@Attribute(index = 1)
	private String errorMsg;

	@Attribute(index = 2)
	private long errorCode;

	@Attribute(index = 3, optional = true)
	private String[] stackTrace;

	public FailureMessage() {
		super(MessageType.Failure);
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public long getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}

	public String[] getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String[] stackTrace) {
		this.stackTrace = stackTrace;
	}

}
