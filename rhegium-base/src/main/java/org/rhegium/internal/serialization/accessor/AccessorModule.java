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
