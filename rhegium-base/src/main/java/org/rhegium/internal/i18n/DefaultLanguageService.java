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
package org.rhegium.internal.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.rhegium.api.i18n.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DefaultLanguageService implements LanguageService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultLanguageService.class);

	private static final Locale FALLBACK_LANGUAGE_ISO = Locale.GERMAN;

	private final Map<String, ResourceBundle> resourceBundles = new HashMap<String, ResourceBundle>();

	private final String languageBundleName;

	@Inject
	public DefaultLanguageService(@Named("LanguageBundleName") String languageBundleName) {
		resourceBundles.put(FALLBACK_LANGUAGE_ISO.getISO3Language(),
				ResourceBundle.getBundle(languageBundleName, FALLBACK_LANGUAGE_ISO));

		this.languageBundleName = languageBundleName;
	}

	@Override
	public Locale getDefaultLocale() {
		return FALLBACK_LANGUAGE_ISO;
	}

	@Override
	public String getString(String key) {
		return getString(key, FALLBACK_LANGUAGE_ISO);
	}

	@Override
	public String getString(String key, Locale locale) {
		ResourceBundle bundle = resourceBundles.get(locale.getISO3Language());
		if (bundle == null) {
			try {
				bundle = ResourceBundle.getBundle(languageBundleName, locale);
				resourceBundles.put(locale.getISO3Language(), bundle);
			}
			catch (MissingResourceException e) {
				bundle = resourceBundles.get(FALLBACK_LANGUAGE_ISO);
			}
		}

		try {
			String value = bundle.getString(key);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Key '" + key + "' with ISO " + locale.getISO3Language() + " resulted in value: " + value);
			}

			return value;
		}
		catch (MissingResourceException e) {
			return "Missing text for key " + key;
		}
	}

}
