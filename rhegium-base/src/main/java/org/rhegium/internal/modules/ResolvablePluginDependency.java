package org.rhegium.internal.modules;

import java.util.Collection;

import org.rhegium.api.modules.FrameworkPlugin;
import org.rhegium.api.modules.PluginDependency;

public interface ResolvablePluginDependency extends
		PluginDependency<PluginClassLoader> {

	boolean isPrioritized();

	boolean isApiBundle();

	Collection<String> getDependencyIds();

	boolean isResolved();

	Collection<ResolvablePluginDependency> resolve(
			final Collection<ResolvablePluginDependency> pluginDescriptors)
			throws Exception;

	Class<? extends FrameworkPlugin> getPluginClass();

}
