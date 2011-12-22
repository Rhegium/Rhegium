package org.rhegium.internal.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rhegium.api.AbstractService;
import org.rhegium.api.config.Configuration;
import org.rhegium.api.config.ConfigurationProvisionException;
import org.rhegium.api.config.ConfigurationService;
import org.rhegium.api.config.TokenResolverManager;
import org.rhegium.api.typeconverter.TypeConverterManager;
import org.rhegium.internal.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

class DefaultConfigurationService extends AbstractService implements ConfigurationService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultConfigurationService.class);

	private static final Pattern REGEX = Pattern.compile("\\$\\{([a-zA-Z0-9.]*?)\\}");

	private final Map<String, String> properties = new HashMap<String, String>();

	private final WatchServiceTask watchServiceTask = new WatchServiceTask();
	private final WatchService watchService;

	private final String configurationBase;

	@Inject
	private TypeConverterManager typeConverterManager;

	@Inject
	private TokenResolverManager tokenResolverManager;

	@Inject
	DefaultConfigurationService(@Named("configurationBase") String configurationBase) {
		this.configurationBase = configurationBase;

		// Prepare Java 7 FileWatch-Service
		try {
			Path basePath = new File(".").toPath();
			this.watchService = basePath.getFileSystem().newWatchService();
		}
		catch (IOException e) {
			throw new IllegalStateException("WatchService could not be prepared", e);
		}

	}

	@Override
	public void initialized() {
		// Load all properties
		try {
			if (configurationBase == null) {
				throw new IllegalArgumentException("configurationBase may not be null");
			}

			File base = new File(configurationBase);
			if (!base.exists()) {
				throw new IllegalArgumentException("configurationBase must exists");
			}

			// Start Java7 FileWatch-Service
			new Thread(watchServiceTask).start();

			recursiveReadProperties(base);

		}
		catch (Exception e) {
			throw new IllegalArgumentException("Failed to load properties files", e);
		}
	}

	@Override
	public <T extends Enum<T> & Configuration<T>, V> V getProperty(T configuration) {
		return getProperty(configuration, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Enum<T> & Configuration<T>, V> V getProperty(T configuration, String expression) {
		final String value = getProperty0(configuration.getKey(), expression, configuration.getDefaultValue(),
				configuration.getType(), configuration.isMultiKey() && StringUtils.isEmpty(expression));

		try {
			return (V) typeConverterManager.convert(value, configuration.getType());

		}
		catch (final ConfigurationProvisionException e) {
			throw new ConfigurationProvisionException(String.format("Could not provision configuration key %s",
					configuration), e);
		}
	}

	@Override
	public <V> V getProperty(String configurationKey, String expression, Class<V> type) {
		final String value = getProperty0(configurationKey, expression, null, type, !StringUtils.isEmpty(expression));

		try {
			return (V) typeConverterManager.convert(value, type);

		}
		catch (final ConfigurationProvisionException e) {
			throw new ConfigurationProvisionException(String.format("Could not provision configuration key %s",
					configurationKey), e);
		}
	}

	@Override
	public Collection<String> getKeys() {
		return Collections.unmodifiableCollection(properties.keySet());
	}

	@Override
	public void shutdown() {
		try {
			watchService.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getPropertyValue(String key) {
		String value = properties.get(key);
		if (value == null) {
			return null;
		}

		return resolveToken(value);
	}

	private String getProperty0(String key, String expression, String defaultValue, Class<?> type, boolean multiKey) {
		if (multiKey) {
			key = key.replace(".*.", "." + expression + ".");
		}

		String value = properties.get(key);

		if (value != null) {
			return resolveToken(value);
		}

		if (defaultValue != null) {
			return resolveToken(defaultValue);
		}

		if (type.isPrimitive()) {
			if (type.equals(boolean.class)) {
				return "false";
			}

			return "0";
		}

		return "";
	}

	private String resolveToken(final String value) {
		final Matcher matcher = REGEX.matcher(value);

		int start = 0;
		final StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			final String token = matcher.group(1);
			final String resolved = tokenResolverManager.resolveToken(token);

			if (resolved != null) {
				sb.append(value.substring(start, matcher.start()));
				sb.append(resolved);

				start = matcher.end();
			}
		}

		if (sb.length() == 0) {
			return value;
		}

		return sb.append(value.substring(start, value.length())).toString();
	}

	private void recursiveReadProperties(File configuration) throws IOException {
		if (configuration == null) {
			return;
		}

		if (configuration.isDirectory()) {
			LOG.info(StringUtils.join(" ", "Registering directory ", configuration.getName(),
					" for filesystem events..."));

			configuration.toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY);

			LOG.info(StringUtils.join(" ", "Searching directory ", configuration.getName(), " for configurations..."));
			for (File child : configuration.listFiles()) {
				recursiveReadProperties(child);
			}

		}
		else {
			readProperties(configuration);
		}
	}

	private void readProperties(File file) throws IOException {
		if (file == null) {
			return;
		}

		if (!file.getName().toUpperCase().endsWith(".PROPERTIES")) {
			return;
		}

		LOG.info("Reading configurations from " + file.getName());
		final Properties properties = new Properties();
		properties.load(new FileReader(file));

		final Set<Object> keySet = properties.keySet();
		for (Object key : keySet) {
			final String oldValue = this.properties.get(key.toString());
			final String sKey = key.toString();
			final String value = properties.getProperty(sKey);

			if (oldValue != null) {
				LOG.info(StringUtils.join(" ", "Overriding old configuration value '", key.toString(), "' ==> '",
						oldValue, "' with '", value, "'"));
			}

			this.properties.put(sKey, value);
		}
	}

	private class WatchServiceTask implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {
					try {
						WatchKey key = watchService.take();
						for (WatchEvent<?> event : key.pollEvents()) {
							Path path = (Path) event.context();
							if (path.getFileName().toString().toLowerCase().endsWith(".properties")) {
								if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
									LOG.info(StringUtils.join(" ", "Found new properties file: ", path.toString()));
								}
								else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
									LOG.info(StringUtils.join(" ", "Found modified properties file: ", path.toString()));
								}

								readProperties(path.toFile());
							}
						}

						if (!key.reset()) {
							key.cancel();
						}
					}
					catch (IOException e) {
						// Ignore
					}
				}

			}
			catch (ClosedWatchServiceException e) {
				// Ignore
			}
			catch (InterruptedException e) {
				// Ignore
			}
		}
	}

}
