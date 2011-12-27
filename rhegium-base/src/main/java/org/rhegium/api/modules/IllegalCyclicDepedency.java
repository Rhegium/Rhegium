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
package org.rhegium.api.modules;

import org.rhegium.internal.utils.StringUtils;

public class IllegalCyclicDepedency extends RuntimeException {

	private static final long serialVersionUID = -5186279868116796470L;

	private final String cyclic1;
	private final String cyclic2;

	public IllegalCyclicDepedency(final String cyclic1, final String cyclic2) {
		this.cyclic1 = cyclic1;
		this.cyclic2 = cyclic2;
	}

	@Override
	public String getMessage() {
		return StringUtils.join(" ", "Illegal cycle detected (", cyclic1, " => ", cyclic2, " => ", cyclic1, ")");
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}

}
