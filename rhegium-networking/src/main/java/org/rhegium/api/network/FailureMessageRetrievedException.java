package org.rhegium.api.network;

import org.rhegium.api.network.protocol.FailureMessage;

@SuppressWarnings("serial")
public class FailureMessageRetrievedException extends RuntimeException {

	private final FailureMessage failureMessage;
	private final StackTraceElement[] stackTrace;

	public FailureMessageRetrievedException(FailureMessage failureMessage) {
		this.failureMessage = failureMessage;

		if (failureMessage.getStackTrace() != null) {
			stackTrace = new StackTraceElement[failureMessage.getStackTrace().length];

			for (int i = 0; i < stackTrace.length; i++) {
				String element = failureMessage.getStackTrace()[i];
				String[] elements = element.split(":::");

				String declaringClass = elements[0];
				String methodName = elements[1];
				String filename = elements[2];
				int lineNumber = -1;
				try {
					lineNumber = Integer.parseInt(elements[3]);
				}
				catch (NumberFormatException e) {
					// ignore it
				}

				stackTrace[i] = new StackTraceElement(declaringClass, methodName, filename, lineNumber);
			}
		}
		else {
			stackTrace = new StackTraceElement[0];
		}
	}

	public long getErrorCode() {
		return failureMessage.getErrorCode();
	}

	public long getMessageId() {
		return failureMessage.getMessageId();
	}

	@Override
	public String getMessage() {
		return failureMessage.getErrorMsg();
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		StackTraceElement[] temp = new StackTraceElement[stackTrace.length];
		System.arraycopy(stackTrace, 0, temp, 0, stackTrace.length);
		return temp;
	}

}
