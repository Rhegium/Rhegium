package org.rhegium.api.modules;

import java.io.InputStream;
import java.util.Collection;

import org.rhegium.api.Service;

public interface PluginManager extends Service {

	void registerPluginLifecycleListener(PluginLifecycleListener listener);

	void removePluginLifecycleListener(PluginLifecycleListener listener);

	Collection<PluginLifecycleListener> getPluginLifecycleListeners();

	FrameworkPlugin getPlugin(String name);

	ClassLoader getPluginClassLoader(FrameworkPlugin plugin);

	Class<?> loadClass(FrameworkPlugin plugin, String className) throws ClassNotFoundException;

	InputStream loadResourceAsStream(FrameworkPlugin plugin, String path);

	Collection<FrameworkPlugin> getRegisteredPlugins();

	void registerPlugin(FrameworkPlugin plugin);

}
