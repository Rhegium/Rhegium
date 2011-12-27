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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.rhegium.api.modules.IllegalCyclicDepedency;
import org.rhegium.internal.utils.ReflectionUtils;
import org.rhegium.internal.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginClassLoader extends WeavingURLClassLoader {

	private static final Logger LOG = LoggerFactory.getLogger(PluginClassLoader.class);

	private final ReentrantLock lock = new ReentrantLock();

	private final Set<PluginClassLoader> buddyClassLoaders = new HashSet<PluginClassLoader>();

	private Collection<String> exports = null;
	private String pluginName;

	public PluginClassLoader(final URL[] urls, final ClassLoader parent) throws IOException {

		super(urls, parent);

		if (LOG.isDebugEnabled()) {
			LOG.debug(StringUtils.join(" ", "Adding classloader: PluginClassLoader(",
					ReflectionUtils.buildClassLoaderHierachy(this), ")"));
		}
	}

	public boolean addBuddyClassLoader(final PluginClassLoader buddyClassLoader) {
		try {
			lock.lock();
			buddyClassLoader.checkCyclicDependency(this);
			return buddyClassLoaders.add(buddyClassLoader);

		}
		finally {
			lock.unlock();
		}
	}

	public boolean removeBuddyClassLoader(final PluginClassLoader buddyClassLoader) {
		try {
			lock.lock();
			return buddyClassLoaders.remove(buddyClassLoader);

		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {

		if (LOG.isTraceEnabled()) {
			LOG.trace(StringUtils.join(" ", "Searching class ", name, "..."));
		}

		try {
			final Class<?> clazz = super.loadClass(name, resolve);
			if (clazz != null) {
				if (LOG.isTraceEnabled()) {
					LOG.trace(StringUtils.join(" ", "Found class ", name, " in parent classLoader."));
				}

				return clazz;
			}

		}
		catch (final ReflectiveOperationException e) {
			try {
				lock.lock();
				if (LOG.isTraceEnabled()) {
					LOG.trace(StringUtils.join(" ", "Available buddyClassLoaders for ", pluginName, ": ",
							buddyClassLoadersToString()));
				}

				for (final PluginClassLoader buddyClassLoader : buddyClassLoaders) {
					final Collection<String> buddyExports = buddyClassLoader.exports;
					if (buddyExports != null) {
						if (LOG.isTraceEnabled()) {
							LOG.trace(StringUtils.join(" ", "Searching class ", name, " in buddyClassLoader '",
									buddyClassLoader.pluginName, "'..."));
						}

						final Iterator<String> iterator = buddyExports.iterator();

						while (iterator.hasNext()) {
							final String export = iterator.next();

							// If export is a package
							if (export.endsWith("*")) {
								// If requested class is not in exported package
								// move on to next possible package
								if (!name.startsWith(export.substring(0, export.length() - 2))) {

									continue;
								}

							}
							else {
								// If export is a single class the canonical
								// name must completely match!
								if (!name.equals(export)) {
									continue;
								}
							}

							try {
								if (LOG.isTraceEnabled()) {
									LOG.trace(StringUtils.join(" ", "Trying to load class ", name,
											" in buddyClassLoader '", buddyClassLoader.pluginName, "'..."));
								}

								final Class<?> buddyClass = buddyClassLoader.loadClass(name);

								if (buddyClass != null) {
									return buddyClass;
								}
							}
							catch (final ReflectiveOperationException ex) {
								// ignore and try next buddyClassLoader
							}
						}
					}
				}

			}
			finally {
				lock.unlock();
			}
		}

		throw new ClassNotFoundException(StringUtils.join(" ", "Class ", name,
				" could not be found on plugin classpath for plugin '", pluginName, "'"));
	}

	void setExports(final Collection<String> exports) {
		if (exports != null) {
			this.exports = Collections.unmodifiableList(new ArrayList<String>(exports));
		}
	}

	void setPluginName(final String pluginName) {
		this.pluginName = pluginName;
	}

	private void checkCyclicDependency(final PluginClassLoader possibleCyclicClassLoader) {

		if (buddyClassLoaders.contains(possibleCyclicClassLoader)) {
			throw new IllegalCyclicDepedency(possibleCyclicClassLoader.pluginName, pluginName);
		}
	}

	private String buddyClassLoadersToString() {
		final StringBuilder sb = new StringBuilder("[");

		boolean first = true;
		for (final PluginClassLoader bcl : buddyClassLoaders) {
			if (first) {
				first = false;
			}
			else {
				sb.append(", ");
			}

			sb.append(bcl.pluginName);
		}

		return sb.append("]").toString();
	}

	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {

		// System.err.println("? PluginClassLoader.findClass(" + name + ")");
		try {
			final byte[] bytes = getBytes(name);
			if (bytes != null) {
				return defineClass(name, bytes);
			}
			else {
				throw new ClassNotFoundException(name);
			}
		}
		catch (final IOException ex) {
			throw new ClassNotFoundException(name);
		}
	}

	private Class<?> defineClass(final String name, final byte[] bytes) throws IOException {

		final String packageName = getPackageName(name);
		if (packageName != null) {
			final Package pakkage = getPackage(packageName);
			if (pakkage == null) {
				definePackage(packageName, null, null, null, null, null, null, null);
			}
		}

		return defineClass(name, bytes);
	}

	private String getPackageName(final String className) {
		final int offset = className.lastIndexOf('.');
		return (offset == -1) ? null : className.substring(0, offset);
	}

}
