package org.rhegium.internal.serialization;

import java.util.Comparator;

class AttributeComparator implements Comparator<AttributeDescriptor> {

	@Override
	public int compare(AttributeDescriptor attribute1, AttributeDescriptor attribute2) {
		return Integer.valueOf(attribute1.getProtocolAttribute().index()).compareTo(
				attribute2.getProtocolAttribute().index());
	}

}
