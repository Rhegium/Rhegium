package org.rhegium.internal.lifecycle;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.TypeEncounter;

/**
 * EncounterProvider taken from GuicyFruit project
 */
public abstract class EncounterProvider<T> {

	public abstract Provider<? extends T> get(TypeEncounter<?> encounter);

	public static <T> EncounterProvider<T> encounterProvider(final Key<? extends T> key) {
		return new EncounterProvider<T>() {

			public Provider<? extends T> get(TypeEncounter<?> encounter) {
				return encounter.getProvider(key);
			}
		};
	}

	public static <T> EncounterProvider<T> encounterProvider(final Class<? extends T> type) {
		return new EncounterProvider<T>() {

			public Provider<? extends T> get(TypeEncounter<?> encounter) {
				return encounter.getProvider(type);
			}
		};
	}

	public static <T> EncounterProvider<T> encounterProvider(final T instance) {
		return new EncounterProvider<T>() {

			public Provider<? extends T> get(TypeEncounter<?> encounter) {
				return new Provider<T>() {

					public T get() {
						return instance;
					}
				};
			}
		};
	}

}
