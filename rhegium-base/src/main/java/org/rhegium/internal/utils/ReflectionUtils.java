package org.rhegium.internal.utils;

public final class ReflectionUtils {

	private ReflectionUtils() {
	}

	public static final String buildClassLoaderHierachy(final ClassLoader classLoader) {
		return new StringBuilder(classLoader.getClass().getCanonicalName()).append(
				(classLoader.getParent() != null ? new StringBuilder(" => ").append(
						buildClassLoaderHierachy(classLoader.getParent())).toString() : "")).toString();
	}
}
