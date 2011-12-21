package org.rhegium.internal.modules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.sourceprojects.lycia.LyciaParser;
import org.sourceprojects.lycia.fluent.FluentBuilder;

public final class PluginContextHelper {

	private static final Logger LOG = Logger.getLogger(PluginContextHelper.class);

	private static final FluentBuilder<PluginLyciaContextObject> BUILDER = FluentBuilder
			.<PluginLyciaContextObject> prepare().parser(FluentBuilder.pojoParser(new PluginXmlLyciaParser()))
			.configure(FluentBuilder.validateSchema(false));

	private PluginContextHelper() {
	}

	public static final PluginClassLoader buildPluginClassLoader(final File pluginPath, final File workPath,
			final ClassLoader parent) {

		try {
			return new PluginClassLoader(findAllPluginJars(pluginPath, workPath), parent);

		}
		catch (final IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static final PluginDescriptor buildPluginDescriptor(
			final File pluginPath, final File workPath, final ClassLoader parent) {

		final PluginClassLoader pluginClassLoader = buildPluginClassLoader(
				pluginPath, workPath, parent);

		final PluginLyciaContextObject contextObject = new PluginLyciaContextObject(
				pluginClassLoader);

		final LyciaParser<PluginLyciaContextObject> parser = BUILDER.configure(
				FluentBuilder.contextObject(contextObject)).build();

		try (final InputStream is = pluginClassLoader
				.getResourceAsStream("META-INF/yujin-plugin.xml")) {

			parser.parse(is);

			final PluginDescriptor pluginDescriptor = contextObject
					.getPluginDescriptor();

			final PluginClassLoader pcl = contextObject.getClassLoader();
			pcl.setExports(pluginDescriptor.getExports());
			pcl.setPluginName(pluginDescriptor.getName());

			return pluginDescriptor;

		}
		catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public final static Collection<ResolvablePluginDependency> precheckAndReorderPluginDescriptors(
			final Collection<ResolvablePluginDependency> pluginDescriptors) {

		checkForDuplicateIds(pluginDescriptors);

		final List<ResolvablePluginDependency> reordered = new ArrayList<ResolvablePluginDependency>();
		for (final ResolvablePluginDependency pluginDescriptor : preorderByPrioritized(pluginDescriptors)) {
			if (!pluginDescriptor.isResolved()) {
				try {
					reordered.addAll(pluginDescriptor.resolve(pluginDescriptors));

				}
				catch (final Exception e) {
					throw new RuntimeException("Plugin " + pluginDescriptor.getId() + " has unresolved dependencies", e);
				}
			}
		}

		return reordered;
	}

	public static final URL[] findAllPluginJars(final File pluginPath, final File workPath) throws IOException {

		if (pluginPath == null) {
			throw new IllegalArgumentException("PluginPath cannot be null");
		}

		if (pluginPath.isFile()) {
			final String filename = pluginPath.getName().toLowerCase();
			if (filename.endsWith(".jar")) {
				LOG.info("Deploy plugin from JAR file " + pluginPath.getAbsolutePath());

				return new URL[] { pluginPath.toURI().toURL() };
			}

			if (filename.endsWith(".zip")) {
				LOG.info("Deploy plugin from ZIP file " + pluginPath.getAbsolutePath());

				final File deploymentPath = new File(workPath, filename);
				if (deploymentPath.exists()) {
					throw new IllegalStateException("Deployment directory '" + deploymentPath.getAbsolutePath()
							+ "' cannot exists - Is there " + "some other instance running?");
				}

				deploymentPath.mkdirs();
				extractZipToFolder(pluginPath, deploymentPath);

				final List<URL> jars = findAllPluginJars0(deploymentPath);
				return jars.toArray(new URL[jars.size()]);
			}
		}

		LOG.info("Deploy plugin from directory " + pluginPath.getAbsolutePath());

		final List<URL> jars = findAllPluginJars0(pluginPath);
		return jars.toArray(new URL[jars.size()]);
	}

	private static void extractZipToFolder(final File pluginPath,
			final File deploymentPath) throws IOException {

		final ZipFile zipFile = new ZipFile(pluginPath);
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			final File target = new File(deploymentPath, entry.getName());

			if (entry.isDirectory()) {
				target.mkdirs();

			} else {
				try (final InputStream input = zipFile.getInputStream(entry);
						final FileOutputStream output = new FileOutputStream(
								target)) {

					final byte[] buffer = new byte[1024];
					int nrBytesRead;

					while ((nrBytesRead = input.read(buffer)) > 0) {
						output.write(buffer, 0, nrBytesRead);
					}
				}
			}
		}
	}

	private static List<URL> findAllPluginJars0(final File pluginPath) throws IOException {
		final List<URL> jars = new ArrayList<URL>();
		for (final File entry : pluginPath.listFiles()) {
			if (entry.isDirectory()) {
				jars.addAll(findAllPluginJars0(entry));

			}
			else if (entry.isFile() && entry.getName().toLowerCase().endsWith(".jar")) {

				jars.add(entry.toURI().toURL());
			}
		}

		return jars;
	}

	private static Collection<ResolvablePluginDependency> preorderByPrioritized(
			Collection<ResolvablePluginDependency> pluginDescriptors) {

		final List<ResolvablePluginDependency> descriptors = new ArrayList<ResolvablePluginDependency>();
		final List<ResolvablePluginDependency> temp = new ArrayList<ResolvablePluginDependency>();

		for (final ResolvablePluginDependency descriptor : pluginDescriptors) {
			if (descriptor.isPrioritized()) {
				descriptors.add(descriptor);
			}
			else {
				temp.add(descriptor);
			}
		}

		descriptors.addAll(temp);

		return descriptors;
	}

	private static void checkForDuplicateIds(final Collection<ResolvablePluginDependency> pluginDescriptors) {
		final Set<ResolvablePluginDependency> ids = new HashSet<ResolvablePluginDependency>();

		final Iterator<ResolvablePluginDependency> iterator = pluginDescriptors.iterator();

		while (iterator.hasNext()) {
			final ResolvablePluginDependency pluginDescriptor = iterator.next();

			boolean addPluginDescriptor = true;
			for (final ResolvablePluginDependency descriptor : ids) {
				if (descriptor.getId().equals(pluginDescriptor.getId())) {
					if (descriptor.getPluginClass().equals(pluginDescriptor.getPluginClass())) {

						LOG.warn("Found same plugin descriptor '" + pluginDescriptor.getId()
								+ "' in multiple classloaders...");

						addPluginDescriptor = false;
						iterator.remove();
						break;
					}

					throw new RuntimeException("Dupplicate Id '" + pluginDescriptor.getId() + "' in plugins ['"
							+ descriptor.getName() + "', '" + pluginDescriptor.getName() + "']");
				}
			}

			if (addPluginDescriptor) {
				ids.add(pluginDescriptor);
			}
		}
	}
}
