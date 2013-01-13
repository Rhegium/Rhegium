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
package org.rhegium.internal.lifecycle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.rhegium.api.AbstractService;
import org.rhegium.api.lifecycle.LifecycleAware;
import org.rhegium.api.lifecycle.LifecycleManager;
import org.rhegium.internal.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

class DefaultLifecycleManager extends AbstractService implements LifecycleManager {

	private static final Logger LOG = LoggerFactory.getLogger(LifecycleManager.class);

	private final Set<LifecycleAware> lifecycleAwares = new CopyOnWriteArraySet<LifecycleAware>();

	private final String version;

	private final AtomicBoolean shutdown = new AtomicBoolean(false);

	private Calendar calendar;

	DefaultLifecycleManager() throws IOException {
		try (final InputStream is = LifecycleManager.class.getClassLoader().getResourceAsStream("META-INF/version.properties")) {

			if (is != null) {
				final Properties properties = new Properties();
				properties.load(is);
				version = properties.getProperty("org.rhegium.version");
			}
			else {
				version = "unknown";
			}
		}
	}

	@Override
	public String getUptime() {
		long uptime = calculateUptime() / 1000;

		final String seconds = String.valueOf(uptime % 60);
		final String minutes = String.valueOf((uptime /= 60) % 60);
		final String hours = String.valueOf((uptime /= 60) % 24);
		final String days = String.valueOf(uptime / 24);

		return StringUtils.join(" ", days, "days,", hours, "hours,", minutes, "minutes,", seconds, "seconds");
	}

	@Override
	public Date getStartTime() {
		return new Date(calendar.getTime().getTime());
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void registerLifecycleAware(final LifecycleAware lifecycleAware) {
		if (lifecycleAware == this) {
			return;
		}

		lifecycleAwares.add(lifecycleAware);
	}

	@Override
	public void removeLifecycleAware(final LifecycleAware lifecycleAware) {
		lifecycleAwares.remove(lifecycleAware);
	}

	@Override
	public Collection<LifecycleAware> getLifecycleAwares() {
		return Collections.unmodifiableCollection(lifecycleAwares);
	}

	@Override
	public void initialized() throws Exception {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));

		calendar = Calendar.getInstance();

		notifyOnInitialize();
	}

	@Override
	public void start() throws Exception {
		notifyOnStartup();

		LOG.info("Startup successfully completed...");
	}

	@Override
	public void shutdown() throws Exception {
		shutdown(0);
	}

	@Override
	public void shutdown(final long timeout) throws Exception {
		if (shutdown.get()) {
			return;
		}
		shutdown.set(true);

		LOG.info("Starting shutdown...");

		try {
			// Shutdown Plugins
			notifyOnShutdown();

		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private long calculateUptime() {
		final Calendar now = Calendar.getInstance();

		final long endL = now.getTimeInMillis() + now.getTimeZone().getOffset(now.getTimeInMillis());

		final long startL = calendar.getTimeInMillis() + calendar.getTimeZone().getOffset(calendar.getTimeInMillis());

		return endL - startL;
	}

	private void notifyOnInitialize() throws Exception {
		final Iterator<LifecycleAware> iterator = lifecycleAwares.iterator();

		while (iterator.hasNext()) {
			iterator.next().initialized();
		}
	}

	private void notifyOnStartup() throws Exception {
		final Iterator<LifecycleAware> iterator = lifecycleAwares.iterator();

		while (iterator.hasNext()) {
			iterator.next().start();
		}
	}

	private void notifyOnShutdown() throws Exception {
		final Iterator<LifecycleAware> iterator = lifecycleAwares.iterator();

		while (iterator.hasNext()) {
			iterator.next().shutdown();
		}
	}

	private class ShutdownHook extends Thread {

		private final LifecycleManager lifecycleManager;

		private ShutdownHook(final LifecycleManager lifecycleManager) {
			this.lifecycleManager = lifecycleManager;
		}

		@Override
		public void run() {
			try {
				lifecycleManager.shutdown(2000);

				notifyOnShutdown();

				SLF4JBridgeHandler.uninstall();

			}
			catch (final Exception e) {
				throw new RuntimeException("Error while shutdown", e);
			}
		}
	}
}
