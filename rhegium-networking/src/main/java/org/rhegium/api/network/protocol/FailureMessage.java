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
package org.rhegium.api.network.protocol;

import org.rhegium.api.network.AbstractMessage;
import org.rhegium.api.network.MessageType;
import org.rhegium.api.serialization.Attribute;

public class FailureMessage extends AbstractMessage {

	private static final MessageType FAILURE = new MessageType() {

		@Override
		public long getMessageTypeId() {
			return Long.MIN_VALUE;
		}
	};

	@Attribute(index = 1)
	private String errorMsg;

	@Attribute(index = 2)
	private long errorCode;

	@Attribute(index = 3, optional = true)
	private String[] stackTrace;

	public FailureMessage() {
		super(FAILURE);
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
