package org.rhegium.internal.modules;


public class PluginLyciaContextObject {

	private final PluginClassLoader classLoader;

	private PluginDescriptor pluginDescriptor = null;

	public PluginLyciaContextObject(final PluginClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public PluginDescriptor getPluginDescriptor() {
		return pluginDescriptor;
	}

	public void setPluginDescriptor(final PluginDescriptor pluginDescriptor) {
		this.pluginDescriptor = pluginDescriptor;
	}

	public PluginClassLoader getClassLoader() {
		return classLoader;
	}

}
