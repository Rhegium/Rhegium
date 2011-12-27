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
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.rhegium.api.mvc.View;
import org.rhegium.api.uibinder.AbstractUiBinderService;
import org.rhegium.api.uibinder.UiBinderException;
import org.rhegium.api.uibinder.UiBinderSaxHandler;
import org.xml.sax.SAXException;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class VaadinUiBinderService extends AbstractUiBinderService<Component> {

	@Override
	protected Component bind(Component component, InputStream stream, Object injectee, View<Component, ?, ?> view, Locale locale) {
		try {
			// Retrieve SAX parser instance
			SAXParser saxParser = getSaxParserFactory().newSAXParser();

			// Initialize TargetHandlers
			ComponentHandler componentHandler = new ComponentHandler(this, view, locale, getTypeConverterManager());
			ComponentIdHandler componentIdHandler = new ComponentIdHandler(componentHandler);
			ResourceHandler resourceHandler = new ResourceHandler(componentHandler, component.getApplication());
			EventBusHandler eventBusHandler = new EventBusHandler(view, componentHandler, getUiBinderEventService());
			LanguageBindingHandler languageBindingHandler = new LanguageBindingHandler(getLanguageService(), componentHandler,
					locale);

			// Initialize SAX handler
			UiBinderSaxHandler saxHandler = new UiBinderSaxHandler(componentHandler, componentIdHandler, resourceHandler,
					eventBusHandler, languageBindingHandler);

			// Parse stream
			saxParser.parse(stream, saxHandler);

			// Inject bindings
			injectBindings(injectee, componentIdHandler.getComponents());

			return component;
		}
		catch (ParserConfigurationException e) {
			throw new UiBinderException(e);
		}
		catch (SAXException e) {
			throw new UiBinderException(e);
		}
		catch (IOException e) {
			throw new UiBinderException(e);
		}
	}

	@Override
	protected Component newBaseComposite() {
		return new BaseComposite();
	}

	@Override
	protected Component prepareView(View<Component, ?, ?> view) {
		// Get view context pane
		VerticalLayout content = (VerticalLayout) view.getComponent();

		// Remove all existing components since we read the content from xml
		content.removeAllComponents();

		return content;
	}

}
