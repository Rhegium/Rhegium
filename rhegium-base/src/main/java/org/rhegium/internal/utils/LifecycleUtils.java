package org.rhegium.internal.utils;

import org.rhegium.api.lifecycle.LifecycleAware;

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
			((LifecycleAware) value).initialized();
		}
	}

	private static void notifyStartup(Object value) {
		if (isLifecycleAware(value)) {
			((LifecycleAware) value).start();
		}
	}

	private static void notifyShutdown(Object value) {
		if (isLifecycleAware(value)) {
			((LifecycleAware) value).shutdown();
		}
	}

	private static boolean isLifecycleAware(Object value) {
		return LifecycleAware.class.isAssignableFrom(value.getClass());
	}

}
