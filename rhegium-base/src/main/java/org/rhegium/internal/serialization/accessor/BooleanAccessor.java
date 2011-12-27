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
package org.rhegium.internal.serialization.accessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.rhegium.api.serialization.accessor.AbstractAccessor;

class BooleanAccessor extends AbstractAccessor<Boolean> {

	@Override
	public boolean acceptType(Class<?> type) {
		return Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type);
	}

	@Override
	public void writeValue(DataOutput stream, Boolean value) throws IOException {
		stream.writeBoolean(value.booleanValue());
	}

	@Override
	public Boolean readValue(DataInput stream, Class<?> type) throws IOException {
		return Boolean.valueOf(stream.readBoolean());
	}

	@Override
	public int getSerializedSize() {
		return 1;
	}

}
