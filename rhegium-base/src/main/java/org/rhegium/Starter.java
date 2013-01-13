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
package org.rhegium;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class Starter {

	private static final String PROPERTIES_BOOTSTRAP_FIREWALLING_CLASSLOADER = "bootstrap.firewalling.classloader";
	private static final String PROPERTIES_BOOTSTRAP_FRAMEWORK_CLASSLOADER = "bootstrap.framework.classloader";
	private static final String PROPERTIES_BOOTSTRAP_FRAMEWORK_FOLDER = "bootstrap.framework.folder";
	private static final String PROPERTIES_FRAMEWORK_ENTRY_POINT = "bootstrap.framework.entry.point";
	private static final String PROPERTIES_FRAMEWORK_BOOTSTRAP_CLASS = "bootstrap.framework.bootstrap.class";
	private static final String PROPERTIES_BOOT_DELEGATION_PACKAGES = "bootstrap.delegation.packages";

	private static final String STANDARD_FIREWALLING_CLASS_LOADER_CLASS = "org.rhegium.FirewallingClassLoader";
	private static final String STANDARD_FRAMEWORK_CLASS_LOADER_CLASS = "org.rhegium.FrameworkClassLoader";

	private static final String STANDARD_FRAMEWORK_BOOTSTRAP_CLASS = "org.rhegium.api.bootstrap.Bootstrapper";

	private static final String STANDARD_CONFIGURATION_FOLDER = "conf";
	private static final String STANDARD_FRAMEWORK_LIBRARY_FOLDER = "lib/framework";

	private static final String FIREWALLING_CLASS_LOADER_CLASS;
	private static final String FRAMEWORK_CLASS_LOADER_CLASS;
	private static final String FRAMEWORK_BOOTSTRAP_CLASS;
	private static final String FRAMEWORK_ENTRY_POINT;
	private static final String FRAMEWORK_LIBRARY_FOLDER;

	private static final String[] JRE_BOOT_DELEGATION_PACKAGES;

	static {
		try {
			System.out.println("Loading bootstrapping properties...");
			final Properties properties = loadProperties();

			final String jreBootDelegationPackages = properties.getProperty(PROPERTIES_BOOT_DELEGATION_PACKAGES);

			JRE_BOOT_DELEGATION_PACKAGES = trimPackages(jreBootDelegationPackages.split(","));

			FIREWALLING_CLASS_LOADER_CLASS = getProperty(properties, PROPERTIES_BOOTSTRAP_FIREWALLING_CLASSLOADER, STANDARD_FIREWALLING_CLASS_LOADER_CLASS);

			FRAMEWORK_CLASS_LOADER_CLASS = getProperty(properties, PROPERTIES_BOOTSTRAP_FRAMEWORK_CLASSLOADER, STANDARD_FRAMEWORK_CLASS_LOADER_CLASS);

			FRAMEWORK_BOOTSTRAP_CLASS = getProperty(properties, PROPERTIES_FRAMEWORK_BOOTSTRAP_CLASS, STANDARD_FRAMEWORK_BOOTSTRAP_CLASS);

			FRAMEWORK_ENTRY_POINT = getProperty(properties, PROPERTIES_FRAMEWORK_ENTRY_POINT, null);

			FRAMEWORK_LIBRARY_FOLDER = getProperty(properties, PROPERTIES_BOOTSTRAP_FRAMEWORK_FOLDER, STANDARD_FRAMEWORK_LIBRARY_FOLDER);
		}
		catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static final void main(String[] args) throws Exception {
		if (FRAMEWORK_ENTRY_POINT == null) {
			throw new IllegalStateException("Entry point in framework.properties must be set!");
		}

		// Retrieve the Firewalling ClassLoader which is a child classloader of
		// the general applications classloader
		final ClassLoader firewallingClassLoader = loadFirewallingClassLoader();

		// Retrieve the Framework ClassLoader which is a child of our
		// firewalling classloader
		final ClassLoader frameworkClassLoader = loadFrameworkClassLoader(firewallingClassLoader);
		Thread.currentThread().setContextClassLoader(frameworkClassLoader);

		// Load up base framework start class and kick of initialization
		kickOffFrameworkBootstrapping(args, frameworkClassLoader);
	}

	private static void kickOffFrameworkBootstrapping(String[] args, ClassLoader frameworkClassLoader) throws Exception {
		final Class<?> bootstrapClass = frameworkClassLoader.loadClass(FRAMEWORK_BOOTSTRAP_CLASS);

		final Class<?> kickOffClazz = frameworkClassLoader.loadClass(FRAMEWORK_ENTRY_POINT);
		if (!bootstrapClass.isAssignableFrom(kickOffClazz)) {
			throw new IllegalArgumentException(PROPERTIES_FRAMEWORK_ENTRY_POINT + " must be an implementation of " + "org.rhegium.api.bootstrap.Bootstrapper");
		}

		final Method initializer = kickOffClazz.getMethod("start", String[].class, ClassLoader.class);
		final Object bootstrapper = kickOffClazz.newInstance();

		System.out.println("Kicking off framework initialization...");
		initializer.invoke(bootstrapper, args, frameworkClassLoader);
	}

	private static ClassLoader loadFirewallingClassLoader() throws Exception {
		final Class<ClassLoader> firewallingClassLoaderClass = loadFirewallingClassLoaderClass();
		final Constructor<ClassLoader> constructor = firewallingClassLoaderClass.getConstructor(String[].class, ClassLoader.class);

		System.out.println("Building FirewallingClassLoader...");

		return constructor.newInstance(JRE_BOOT_DELEGATION_PACKAGES, Starter.class.getClassLoader());
	}

	@SuppressWarnings("unchecked")
	private static Class<ClassLoader> loadFirewallingClassLoaderClass() throws ClassNotFoundException {
		return (Class<ClassLoader>) Class.forName(FIREWALLING_CLASS_LOADER_CLASS);
	}

	private static ClassLoader loadFrameworkClassLoader(final ClassLoader firewallingClassLoader) throws Exception {

		final File frameworkDirectory = new File(FRAMEWORK_LIBRARY_FOLDER);
		final URL[] entries = buildFrameworkClasspath(frameworkDirectory);
		final Class<ClassLoader> frameworkClassLoaderClass = loadFrameworkClassLoaderClass(firewallingClassLoader);
		final Constructor<ClassLoader> constructor = frameworkClassLoaderClass.getConstructor(URL[].class, ClassLoader.class);

		System.out.println("Building FrameworkClassLoader...");

		return constructor.newInstance(entries, firewallingClassLoader);
	}

	@SuppressWarnings("unchecked")
	private static Class<ClassLoader> loadFrameworkClassLoaderClass(final ClassLoader parent) throws ClassNotFoundException {
		return (Class<ClassLoader>) Class.forName(FRAMEWORK_CLASS_LOADER_CLASS);
	}

	private static URL[] buildFrameworkClasspath(final File directory) throws IOException {
		final List<URL> entries = new ArrayList<>();

		// Add configuration directory
		entries.add(new File(getConfigurationBase()).toURI().toURL());

		// Add JAR files
		entries.addAll(findAllJars(directory));

		return entries.toArray(new URL[entries.size()]);
	}

	private static List<URL> findAllJars(final File directory) throws MalformedURLException {
		final List<URL> jars = new ArrayList<>();

		if (!directory.exists()) {
			return jars;
		}

		for (final File entry : directory.listFiles()) {
			if (entry.isDirectory()) {
				jars.addAll(findAllJars(entry));

			}
			else if (entry.isFile() && entry.getName().toLowerCase().endsWith(".jar")) {
				String override = System.getProperty("org.rhegium.override." + entry.getName());
				if (override != null) {
					File overrideFile = new File(override);
					if (!overrideFile.exists()) {
						throw new MalformedURLException("Override for " + entry.getName() + "'" + override + "' is no legal file or directory");
					}

					jars.add(overrideFile.toURI().toURL());
				}
				else {
					jars.add(entry.toURI().toURL());
				}
			}
		}

		return jars;
	}

	private static Properties loadProperties() throws IOException {
		final File configDirectory = new File(getConfigurationBase());
		final Properties properties = new Properties();
		properties.load(new FileReader(new File(configDirectory, "framework.properties")));

		return properties;
	}

	private static String getConfigurationBase() {
		String configurationBase = System.getProperty("org.rhegium.configurationBase");
		if (configurationBase == null) {
			configurationBase = STANDARD_CONFIGURATION_FOLDER;
		}
		return configurationBase;
	}

	private static String getProperty(final Properties properties, final String propertyName, final String defaultValue) {
		final String value = System.getProperty(propertyName);
		if (value != null && !value.isEmpty()) {
			return value;
		}

		return properties.getProperty(propertyName, defaultValue);
	}

	private static String[] trimPackages(final String[] packages) {
		final String[] temp = new String[packages.length];

		for (int i = 0; i < packages.length; i++) {
			temp[i] = packages[i].trim();
		}

		return temp;
	}

}
