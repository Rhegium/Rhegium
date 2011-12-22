package org.rhegium.internal.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.rhegium.api.i18n.LanguageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLanguageService implements LanguageService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultLanguageService.class);

	private static final String RESOURCE_BUNDLE_BASE_NAME = "de.heldenreich.wcc.Messages";
	private static final Locale FALLBACK_LANGUAGE_ISO = Locale.GERMAN;

	private final Map<String, ResourceBundle> resourceBundles = new HashMap<String, ResourceBundle>();

	public DefaultLanguageService() {
		resourceBundles.put(FALLBACK_LANGUAGE_ISO.getISO3Language(),
				ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, FALLBACK_LANGUAGE_ISO));
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
				bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale);
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
