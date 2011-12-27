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
package org.rhegium.internal.typeconverter;

import org.rhegium.api.typeconverter.TypeConverter;
import org.rhegium.api.typeconverter.TypeConverterManager;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class TypeconverterModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TypeConverterManager.class).to(DefaultTypeConverterManager.class).in(Singleton.class);

		Multibinder<TypeConverter> multibinder = Multibinder.newSetBinder(binder(), TypeConverter.class);
		multibinder.addBinding().to(BaseTypeConverter.class);
		multibinder.addBinding().to(StringArrayTypeConverter.class);
	}

}
