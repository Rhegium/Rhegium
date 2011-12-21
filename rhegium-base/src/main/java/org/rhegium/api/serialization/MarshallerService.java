package org.rhegium.api.serialization;

public interface MarshallerService {

	<T> Marshaller<T> createMarshaller();

	<T> Unmarshaller<T> createUnmarshaller();

}
