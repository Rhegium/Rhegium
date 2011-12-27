/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rhegium.api.uibinder;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UiBinderSaxHandler extends DefaultHandler {

	private static final IgnoringTargetHandler IGNORING_TARGET_HANDLER = new IgnoringTargetHandler();

	private final Map<String, TargetHandler> targetHandlers = new HashMap<String, TargetHandler>();

	public UiBinderSaxHandler(TargetHandler... targetHandlers) {
		this.targetHandlers.put(null, IGNORING_TARGET_HANDLER);

		for (TargetHandler targetHandler : targetHandlers) {
			this.targetHandlers.put(targetHandler.getTargetNamespace(), targetHandler);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		TargetHandler targetHandler = getTargetHandler(uri);
		targetHandler.handleStartElement(uri, localName);

		for (int i = 0; i < attributes.getLength(); i++) {
			String attributeUri = attributes.getURI(i);
			String attributeName = attributes.getLocalName(i);
			String attributeValue = attributes.getValue(i);

			getTargetHandler(attributeUri).handleAttribute(attributeName, attributeValue);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		getTargetHandler(uri).handleEndElement(uri, localName);
	}

	private TargetHandler getTargetHandler(String uri) {
		if (targetHandlers.containsKey(uri)) {
			return targetHandlers.get(uri);
		}

		return targetHandlers.get(null);
	}

	private static class IgnoringTargetHandler implements TargetHandler {

		@Override
		public String getTargetNamespace() {
			return null;
		}

		@Override
		public void handleStartElement(String uri, String name) {
		}

		@Override
		public void handleEndElement(String uri, String name) {
		}

		@Override
		public void handleAttribute(String name, Object value) {
		}
	}

}
