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
package org.rhegium.internal.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.rhegium.internal.utils.StringUtils;

public class ResourceBundleControl extends Control {

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {

		if (baseName == null || locale == null || format == null || loader == null) {
			throw new NullPointerException();
		}

		String resourceName = StringUtils.join("", getBundleName(baseName, locale), ".properties");

		try (InputStream is = loader.getResourceAsStream(resourceName)) {
			return new PropertyResourceBundle(is);
		}
	}

	private String getBundleName(String baseName, Locale locale) {
		if (locale == Locale.ROOT) {
			return baseName;
		}

		String language = locale.getLanguage();
		String script = locale.getScript();
		String country = locale.getCountry();
		String variant = locale.getVariant();

		if (language == "" && country == "" && variant == "") {
			return baseName;
		}

		StringBuilder sb = new StringBuilder(baseName.replace(".", "/"));
		sb.append('_');
		if (script != "") {
			if (variant != "") {
				sb.append(language).append('_').append(script).append('_').append(country).append('_').append(variant);
			}
			else if (country != "") {
				sb.append(language).append('_').append(script).append('_').append(country);
			}
			else {
				sb.append(language).append('_').append(script);
			}
		}
		else {
			if (variant != "") {
				sb.append(language).append('_').append(country).append('_').append(variant);
			}
			else if (country != "") {
				sb.append(language).append('_').append(country);
			}
			else {
				sb.append(language);
			}
		}

		return sb.toString();
	}

}
