package org.rhegium.api.serialization;

import java.io.DataInput;
import java.io.IOException;

public interface Unmarshaller<O> {

	void unmarshal(O object, DataInput stream) throws IOException;

}
