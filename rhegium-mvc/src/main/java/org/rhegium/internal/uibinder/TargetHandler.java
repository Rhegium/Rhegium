package org.rhegium.internal.uibinder;

interface TargetHandler {

	String getTargetNamespace();

	void handleStartElement(String uri, String name);

	void handleEndElement(String uri, String name);

	void handleAttribute(String name, Object value);

}
