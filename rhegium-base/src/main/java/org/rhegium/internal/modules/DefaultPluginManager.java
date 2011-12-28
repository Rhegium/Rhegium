/*
 * Copyright (C) 2011 Rhegium Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rhegium.internal.modules;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rhegium.api.AbstractService;
import org.rhegium.api.lifecycle.LifecycleManager;
import org.rhegium.api.modules.FrameworkPlugin;
import org.rhegium.api.modules.PluginLifecycleListener;
import org.rhegium.api.modules.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

class DefaultPluginManager extends AbstractService implements PluginManager {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultPluginManager.class);

	private final Set<PluginLifecycleListener> listeners = new CopyOnWriteArraySet<>();
	private final Map<FrameworkPlugin, ClassLoader> classLoaders = new ConcurrentHashMap<>();
	private final Map<String, FrameworkPlugin> plugins = new ConcurrentHashMap<>();

	@Inject
	private LifecycleManager lifecycleManager;

	@Inject
	private Injector injector;

	@Inject
	DefaultPluginManager(LifecycleManager lifecycleManager) {
		lifecycleManager.registerLifecycleAware(this);
	}

	@Override
	public void initialized() throws Exception {
		super.initialized();

		Iterator<FrameworkPlugin> iterator = plugins.values().iterator();
		while (iterator.hasNext()) {
			final FrameworkPlugin plugin = iterator.next();

			try {
				LOG.info(String.format("Configure Plugin '%s'...", plugin.getName()));

				// New inject member instances
				injector.injectMembers(plugin);

			}
			catch (final Exception e) {
				iterator.remove();
				e.printStackTrace();
			}
		}

		iterator = plugins.values().iterator();
		while (iterator.hasNext()) {
			final FrameworkPlugin plugin = iterator.next();

			try {
				LOG.info(String.format("Initialize Plugin '%s'...", plugin.getName()));
				plugin.initialize();

			}
			catch (final Exception e) {
				iterator.remove();
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() throws Exception {
		super.start();

		final Iterator<FrameworkPlugin> iterator = plugins.values().iterator();
		while (iterator.hasNext()) {
			final FrameworkPlugin plugin = iterator.next();

			try {
				LOG.info(String.format("Starting Plugin '%s'...", plugin.getName()));
				plugin.startup();

			}
			catch (final Exception e) {
				iterator.remove();
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();

		final List<FrameworkPlugin> list = new ArrayList<>(plugins.values());
		Collections.reverse(list);

		final Iterator<FrameworkPlugin> iterator = list.iterator();
		while (iterator.hasNext()) {
			final FrameworkPlugin plugin = iterator.next();

			try {
				LOG.info("Stopping Plugin '" + plugin.getName() + "'...");
				plugin.destroy();

			}
			catch (final Exception e) {
				iterator.remove();
				e.printStackTrace();
			}
		}

		lifecycleManager.removeLifecycleAware(this);
	}

	@Override
	public void registerPluginLifecycleListener(PluginLifecycleListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removePluginLifecycleListener(PluginLifecycleListener listener) {
		listeners.remove(listener);
	}

	@Override
	public Collection<PluginLifecycleListener> getPluginLifecycleListeners() {
		return Collections.unmodifiableCollection(listeners);
	}

	@Override
	public FrameworkPlugin getPlugin(String name) {
		return plugins.get(name);
	}

	@Override
	public ClassLoader getPluginClassLoader(FrameworkPlugin plugin) {
		return classLoaders.get(plugin);
	}

	@Override
	public Class<?> loadClass(FrameworkPlugin plugin, String className) throws ClassNotFoundException {
		final ClassLoader classLoader = getPluginClassLoader(plugin);

		if (classLoader == null) {
			return null;
		}

		return classLoader.loadClass(className);
	}

	@Override
	public InputStream loadResourceAsStream(FrameworkPlugin plugin, String path) {
		final ClassLoader classLoader = getPluginClassLoader(plugin);

		if (classLoader == null) {
			return null;
		}

		return classLoader.getResourceAsStream(path);
	}

	@Override
	public Collection<FrameworkPlugin> getRegisteredPlugins() {
		return Collections.unmodifiableCollection(plugins.values());
	}

	@Override
	public void registerPlugin(FrameworkPlugin plugin) {
		plugins.put(plugin.getName(), plugin);
		classLoaders.put(plugin, plugin.getClass().getClassLoader());
	}

}
