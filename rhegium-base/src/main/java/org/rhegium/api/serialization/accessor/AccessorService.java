package org.rhegium.api.serialization.accessor;

public interface AccessorService {

	void registerAccessor(Accessor<?> accessor);

	void removeAccessor(Accessor<?> accessor);

	<T> Accessor<T> resolveAccessor(Class<?> type);

	<T> int getSerializedSize(Class<?> type);

}
