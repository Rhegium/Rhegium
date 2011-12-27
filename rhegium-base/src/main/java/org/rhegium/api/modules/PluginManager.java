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
