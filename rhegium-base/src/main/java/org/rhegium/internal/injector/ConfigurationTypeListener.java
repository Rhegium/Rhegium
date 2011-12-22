package org.rhegium.internal.injector;

import java.lang.reflect.Field;

import org.rhegium.api.config.ConfigurationService;
import org.rhegium.api.injector.Configuration;

import com.google.inject.Inject;
import com.google.inject.MembersInjector;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

class ConfigurationTypeListener implements TypeListener {

	@Inject
	private ConfigurationService configurationService;

	@Override
	@SuppressWarnings("unchecked")
	public <T> void hear(TypeLiteral<T> type, TypeEncounter<T> encounter) {
		Class<T> rawtype = (Class<T>) type.getRawType();

		for (final Field field : rawtype.getDeclaredFields()) {
			if (field.isAnnotationPresent(Configuration.class)) {
				encounter.register(new MembersInjector<T>() {

					@Override
					public void injectMembers(T instance) {
						Configuration configuration = field.getAnnotation(Configuration.class);
						Object value = configurationService.getProperty(configuration.name(),
								configuration.expression(), field.getType());

						try {
							field.set(instance, value);
						}
						catch (IllegalAccessException e) {
							throw new ProvisionException(String.format("Configuration value %s could not be injected",
									configuration), e);
						}
					}
				});
			}
		}
	}

}
