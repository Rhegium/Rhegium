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

import java.util.Locale;

import org.rhegium.api.i18n.LanguageService;
import org.rhegium.api.uibinder.TargetHandler;
import org.rhegium.api.uibinder.UiBinderException;
import org.rhegium.internal.utils.StringUtils;

class LanguageBindingHandler implements TargetHandler {

	private final LanguageService languageService;
	private final ComponentHandler componentHandler;
	private final Locale locale;

	LanguageBindingHandler(LanguageService languageService, ComponentHandler componentHandler, Locale locale) {
		this.languageService = languageService;
		this.componentHandler = componentHandler;
		this.locale = locale;
	}

	@Override
	public String getTargetNamespace() {
		return "urn:de.heldenreich.wcc.framework.mvc.uibinder.i18n";
	}

	@Override
	public void handleStartElement(String uri, String name) {
	}

	@Override
	public void handleEndElement(String uri, String name) {
	}

	@Override
	public void handleAttribute(String name, Object value) {
		if (value == null || !(value instanceof String) || StringUtils.isEmpty(value.toString())) {
			throw new UiBinderException("Illegal attribute value found");
		}

		String message = languageService.getString(value.toString(), locale);
		componentHandler.handleAttribute(name, message);
	}

}
