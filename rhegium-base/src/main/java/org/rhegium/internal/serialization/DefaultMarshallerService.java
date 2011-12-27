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

import org.rhegium.api.serialization.Marshaller;
import org.rhegium.api.serialization.MarshallerService;
import org.rhegium.api.serialization.Unmarshaller;
import org.rhegium.internal.utils.LifecycleUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;

class DefaultMarshallerService implements MarshallerService {

	@Inject
	private Injector injector;

	DefaultMarshallerService() {
	}

	@Override
	public <T> Marshaller<T> createMarshaller() {
		return LifecycleUtils.startLifecycleEntity(new DefaultPojoMarshaller<T>(), injector);
	}

	@Override
	public <T> Unmarshaller<T> createUnmarshaller() {
		return LifecycleUtils.startLifecycleEntity(new DefaultPojoMarshaller<T>(), injector);
	}

}
