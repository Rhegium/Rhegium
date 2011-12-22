package org.rhegium.api.i18n;

import java.util.Locale;

public interface LanguageService {

	String getString(String key);

	String getString(String key, Locale locale);

	Locale getDefaultLocale();

}
