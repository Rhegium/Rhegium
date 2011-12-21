package org.rhegium.api.serialization;

import java.io.DataOutput;
import java.io.IOException;

public interface Marshaller<O> {

	void marshal(O object, DataOutput stream) throws IOException;

}
