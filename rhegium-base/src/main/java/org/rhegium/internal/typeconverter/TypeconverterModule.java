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
