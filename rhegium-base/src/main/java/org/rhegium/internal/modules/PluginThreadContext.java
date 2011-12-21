package org.rhegium.internal.modules;

public abstract class PluginThreadContext<T> {
	private final PluginClassLoader pluginClassLoader;

	public PluginThreadContext(final PluginClassLoader pluginClassLoader) {
		this.pluginClassLoader = pluginClassLoader;
	}

	public T execute() {
		final ClassLoader originalClassLoader = Thread.currentThread()
				.getContextClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(pluginClassLoader);
			return run();

		} finally {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}
	}

	protected abstract T run();

}
