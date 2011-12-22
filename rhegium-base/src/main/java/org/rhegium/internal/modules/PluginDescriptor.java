package org.rhegium.internal.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.rhegium.api.modules.FrameworkPlugin;
import org.rhegium.api.modules.PluginDependency;
import org.rhegium.internal.utils.StringUtils;

public class PluginDescriptor implements ResolvablePluginDependency {

	private static final Logger LOG = Logger.getLogger(PluginDescriptor.class);

	private final String id;
	private final String name;
	private final boolean apiBundle;
	private final Class<? extends FrameworkPlugin> pluginClass;
	private final PluginClassLoader pluginClassLoader;
	private final List<String> dependencyIds;
	private final Collection<PluginDependency<PluginClassLoader>> dependencies = new ArrayList<PluginDependency<PluginClassLoader>>();
	private final boolean prioritized;
	private final List<String> exports;

	private boolean resolved = false;

	public PluginDescriptor(final String id, final String name, final boolean prioritized, final boolean apiBundle,
			final Class<? extends FrameworkPlugin> pluginClass, final PluginClassLoader pluginClassLoader,
			final List<String> dependencyIds, final List<String> exports) {

		this.id = id;
		this.name = name;
		this.prioritized = prioritized;
		this.apiBundle = apiBundle;
		this.pluginClass = pluginClass;
		this.pluginClassLoader = pluginClassLoader;
		this.dependencyIds = dependencyIds;
		this.exports = exports;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isPrioritized() {
		return prioritized;
	}

	@Override
	public boolean isApiBundle() {
		return apiBundle;
	}

	@Override
	public Class<? extends FrameworkPlugin> getPluginClass() {
		return pluginClass;
	}

	@Override
	public PluginClassLoader getPluginClassLoader() {
		return pluginClassLoader;
	}

	@Override
	public Collection<PluginDependency<PluginClassLoader>> getDependencies() {
		return Collections.unmodifiableCollection(dependencies);
	}

	@Override
	public List<String> getDependencyIds() {
		if (dependencyIds == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(dependencyIds);
	}

	@Override
	public Collection<String> getExports() {
		if (exports == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(exports);
	}

	@Override
	public boolean isResolved() {
		return resolved;
	}

	@Override
	public Collection<ResolvablePluginDependency> resolve(Collection<ResolvablePluginDependency> pluginDescriptors)
			throws Exception {

		if (getDependencyIds().size() == 0) {
			setResolved();
			return Arrays.asList(new ResolvablePluginDependency[] { this });
		}

		final List<ResolvablePluginDependency> descriptors = new ArrayList<ResolvablePluginDependency>();
		for (final String dependencyId : getDependencyIds()) {
			for (final ResolvablePluginDependency pluginDescriptor : pluginDescriptors) {
				if (dependencyId.equals(pluginDescriptor.getId())) {
					if (!pluginDescriptor.isResolved()) {
						descriptors.addAll(pluginDescriptor.resolve(pluginDescriptors));

						if (!pluginDescriptor.isResolved()) {
							throw new Exception(StringUtils.join(" ", "Dependency ", pluginDescriptor.getId(),
									" could not be resolved"));
						}
					}
				}
			}
		}

		// Add resolved dependencies
		for (final String dependencyId : getDependencyIds()) {
			for (final ResolvablePluginDependency dependency : descriptors) {
				if (dependencyId.equals(dependency.getId())) {
					dependencies.add(dependency);
				}
			}
			for (final ResolvablePluginDependency dependency : pluginDescriptors) {
				if (dependencyId.equals(dependency.getId())) {
					dependencies.add(dependency);
				}
			}
		}

		// Mark dependencies as fully resolved
		setResolved();

		// Add myself as dependency
		descriptors.add(this);
		return descriptors;
	}

	private void setResolved() {
		LOG.info(StringUtils.join(" ", "Resolved Dependency ", getId(), "..."));
		this.resolved = true;
	}

}
