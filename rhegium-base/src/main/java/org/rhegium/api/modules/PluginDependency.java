package org.rhegium.api.modules;

import java.util.Collection;

public interface PluginDependency<C extends ClassLoader> {

	String getId();

	String getName();

	C getPluginClassLoader();

	Collection<PluginDependency<C>> getDependencies();

	Collection<String> getExports();

}
