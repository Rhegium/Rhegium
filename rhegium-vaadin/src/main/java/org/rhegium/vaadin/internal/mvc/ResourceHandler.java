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
package org.rhegium.vaadin.internal.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.rhegium.api.uibinder.TargetHandler;
import org.rhegium.api.uibinder.UiBinderException;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.terminal.ThemeResource;

class ResourceHandler implements TargetHandler {

	private final ComponentHandler componentHandler;
	private final Application application;

	ResourceHandler(ComponentHandler componentHandler, Application application) {
		this.componentHandler = componentHandler;
		this.application = application;
	}

	@Override
	public String getTargetNamespace() {
		return "urn:de.heldenreich.wcc.framework.mvc.uibinder.resource";
	}

	@Override
	public void handleStartElement(String uri, String name) {
	}

	@Override
	public void handleEndElement(String uri, String name) {
	}

	@Override
	public void handleAttribute(String name, Object value) {
		if (value == null || !(value instanceof String)) {
			throw new UiBinderException("Illegal value found for attribute id " + name);
		}

		String uri = (String) value;
		if (uri.contains("://")) {
			try {
				URI scheme = new URI(uri);

				String schemePart = scheme.getScheme();
				Resource resource;
				if ("theme".equals(schemePart)) {
					resource = new ThemeResource(scheme.getRawPath());
				}
				else if ("file".equals(schemePart)) {
					InputStream is = scheme.toURL().openStream();
					resource = new StreamResource(new InputStreamStreamSource(is), scheme.getRawPath(), application);
				}
				else if ("classpath".equals(schemePart)) {
					InputStream is = getClass().getClassLoader().getResourceAsStream(scheme.getRawPath());
					resource = new StreamResource(new InputStreamStreamSource(is), scheme.getRawPath(), application);
				}
				else {
					resource = new ExternalResource(scheme.getRawPath());
				}

				componentHandler.handleAttribute(name, resource);
			}
			catch (URISyntaxException e) {
				throw new UiBinderException(e);
			}
			catch (MalformedURLException e) {
				throw new UiBinderException(e);
			}
			catch (IOException e) {
				throw new UiBinderException(e);
			}
		}
	}

	@SuppressWarnings("serial")
	private class InputStreamStreamSource implements StreamSource {

		private final InputStream stream;

		InputStreamStreamSource(InputStream stream) {
			this.stream = stream;
		}

		@Override
		public InputStream getStream() {
			return stream;
		}
	}

}
