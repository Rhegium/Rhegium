package org.rhegium.internal.injector;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.rhegium.api.config.ConfigurationProvisionException;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class PostConstructTypeListener implements TypeListener {

	@Override
	@SuppressWarnings("unchecked")
	public <T> void hear(TypeLiteral<T> type, TypeEncounter<T> encounter) {
		Class<T> rawtype = (Class<T>) type.getRawType();

		for (final Method method : rawtype.getDeclaredMethods()) {
			if (method.isAnnotationPresent(PostConstruct.class)) {
				if (method.getParameterTypes().length > 0) {
					throw new ConfigurationProvisionException(String.format(
							"@PostConstruct annotated method %s must not contain "
									+ "any parameters in method signature", method));
				}

				encounter.register(new InjectionListener<T>() {

					@Override
					public void afterInjection(T injectee) {
						try {
							method.invoke(injectee);
						}
						catch (Exception e) {
							throw new ProvisionException(String.format(
									"@PostConstruct annotated method %s could not be called", method), e);
						}
					}
				});
			}
		}
	}
}
