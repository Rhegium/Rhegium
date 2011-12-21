package org.rhegium.internal.serialization;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rhegium.api.serialization.accessor.Accessor;
import org.rhegium.api.serialization.accessor.AccessorService;

import com.google.inject.Inject;

class DefaultAccessorService implements AccessorService {

	private final Set<Accessor<?>> accessors = new CopyOnWriteArraySet<>();

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
