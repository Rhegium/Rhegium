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

import org.rhegium.api.serialization.accessor.Accessor;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

public class AccessorModule extends AbstractModule {

	@Override
	protected void configure() {
		Multibinder<Accessor<?>> multibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<Accessor<?>>() {
		});

		multibinder.addBinding().to(BooleanAccessor.class);
		multibinder.addBinding().to(ByteAccessor.class);
		multibinder.addBinding().to(DateAccessor.class);
		multibinder.addBinding().to(DoubleAccessor.class);
		multibinder.addBinding().to(EnumAccessor.class);
		multibinder.addBinding().to(FloatAccessor.class);
		multibinder.addBinding().to(IntegerAccessor.class);
		multibinder.addBinding().to(ListAccessor.class);
		multibinder.addBinding().to(LongAccessor.class);
		multibinder.addBinding().to(PojoAccessor.class);
		multibinder.addBinding().to(PrimitiveByteArrayAccessor.class);
		multibinder.addBinding().to(ShortAccessor.class);
		multibinder.addBinding().to(StringAccessor.class);
		multibinder.addBinding().to(WrapperByteArrayAccessor.class);
	}

}
