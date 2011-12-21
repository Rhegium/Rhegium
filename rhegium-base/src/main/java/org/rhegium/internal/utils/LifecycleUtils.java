package org.rhegium.internal.utils;

import org.rhegium.api.Lifecycle;

import com.google.inject.Injector;

public final class LifecycleUtils {

	private LifecycleUtils() {
	}

	public static <T> T startLifecycleEntity(T entity, Injector injector) {
		injector.injectMembers(entity);
		return startLifecycleEntity(entity);
	}

	public static <T> T startLifecycleEntity(T entity) {
		notifyInitialized(entity);
		notifyStartup(entity);
		return entity;
	}

	public static <T> void stopLifecycleEntity(T entity) {
		notifyShutdown(entity);
	}

	private static void notifyInitialized(Object value) {
		if (isLifecycleAware(value)) {
			((Lifecycle) value).initialized();
		}
	}

	private static void notifyStartup(Object value) {
		if (isLifecycleAware(value)) {
			((Lifecycle) value).start();
		}
	}

	private static void notifyShutdown(Object value) {
		if (isLifecycleAware(value)) {
			((Lifecycle) value).shutdown();
		}
	}

	private static boolean isLifecycleAware(Object value) {
		return Lifecycle.class.isAssignableFrom(value.getClass());
	}

}
