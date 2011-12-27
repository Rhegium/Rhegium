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
package org.rhegium.internal.network.socket;

import org.rhegium.api.network.MessageType;

public enum TestMessageType implements MessageType {
	TestRequest(1),
	TestResponse(2),

	Ping(3),
	Pong(4),

	;

	private final long messageType;

	private TestMessageType(long messageType) {
		this.messageType = messageType;
	}

	@Override
	public long getMessageTypeId() {
		return messageType;
	}

}
