package org.rhegium.internal.serialization;

import org.rhegium.api.serialization.MarshallerService;
import org.rhegium.api.serialization.accessor.AccessorService;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class SerializationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccessorService.class).to(DefaultAccessorService.class).in(Singleton.class);
		bind(MarshallerService.class).to(DefaultMarshallerService.class).in(Singleton.class);
	}

}
