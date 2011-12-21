package org.rhegium.api.modules;

public interface PluginLifecycleListener {

	void pluginCreated(FrameworkPlugin plugin);

	void pluginInitialized(FrameworkPlugin plugin);

	void pluginStarted(FrameworkPlugin plugin);

	void pluginDestroyed(FrameworkPlugin plugin);

}
