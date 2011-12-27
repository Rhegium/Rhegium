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
package org.rhegium.internal.serialization;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rhegium.api.serialization.accessor.Accessor;
import org.rhegium.api.serialization.accessor.AccessorService;

import com.google.inject.Inject;

class DefaultAccessorService implements AccessorService {

	private final Set<Accessor<?>> accessors = new CopyOnWriteArraySet<Accessor<?>>();

	@Inject
	DefaultAccessorService(Set<Accessor<?>> accessors) {
		this.accessors.addAll(accessors);
	}

	@Override
	public void registerAccessor(Accessor<?> accessor) {
		accessors.add(accessor);
	}

	@Override
	public void removeAccessor(Accessor<?> accessor) {
		accessors.remove(accessor);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Accessor<T> resolveAccessor(Class<?> type) {
		if (type == null) {
			return null;
		}

		for (Accessor<?> accessor : accessors) {
			if (accessor.acceptType(type)) {
				return (Accessor<T>) accessor;
			}
		}

		return null;
	}

	@Override
	public <T> int getSerializedSize(Class<?> type) {
		final Accessor<T> accessor = resolveAccessor(type);
		return accessor == null ? -1 : accessor.getSerializedSize();
	}

}
