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
