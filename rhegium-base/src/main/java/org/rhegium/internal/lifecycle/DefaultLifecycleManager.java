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

import org.rhegium.api.lifecycle.LifecycleAware;
import org.rhegium.api.lifecycle.LifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

class DefaultLifecycleManager implements LifecycleManager {

	private static final Logger LOG = LoggerFactory.getLogger(LifecycleManager.class);

	private final Set<LifecycleAware> lifecycleAwares = new CopyOnWriteArraySet<LifecycleAware>();

	private final String version;

	private Calendar calendar;

	DefaultLifecycleManager() throws IOException {                                                                                                                                        
		try (final InputStream is = LifecycleManager.class.getClassLoader()                                                                                                               
				.getResourceAsStream("META-INF/version.properties")) {                                                                                                                    
                                                                                                                                                                                          
			if (is != null) {                                                                                                                                                             
				final Properties properties = new Properties();                                                                                                                           
				properties.load(is);                                                                                                                                                      
				version = properties.getProperty("com.yujinserver.version");                                                                                                              
			} else {                                                                                                                                                                      
				version = "unknown";                                                                                                                                                      
			}	                                                                                                                                                                          
		}                                                                                                                                                                                 
	}	@Override
	public String getUptime() {
		long uptime = calculateUptime() / 1000;

		final String seconds = String.valueOf(uptime % 60);
		final String minutes = String.valueOf((uptime /= 60) % 60);
		final String hours = String.valueOf((uptime /= 60) % 24);
		final String days = String.valueOf(uptime / 24);

		return new StringBuilder(days).append(" days, ").append(hours).append(" hours, ").append(minutes)
				.append(" minutes, ").append(seconds).append(" seconds").toString();
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
	public void initialize() throws Exception {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(this));

		calendar = Calendar.getInstance();

		notifyOnInitialize();
	}

	@Override
	public void startup() throws Exception {
		notifyOnStartup();

		LOG.info("Startup successfully completed...");
	}

	@Override
	public void shutdown() throws Exception {
		shutdown(0);
	}

	@Override
	public void shutdown(final long timeout) throws Exception {
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

	private void notifyOnInitialize() {
		final Iterator<LifecycleAware> iterator = lifecycleAwares.iterator();

		while (iterator.hasNext()) {
			iterator.next().initialized();
		}
	}

	private void notifyOnStartup() {
		final Iterator<LifecycleAware> iterator = lifecycleAwares.iterator();

		while (iterator.hasNext()) {
			iterator.next().start();
		}
	}

	private void notifyOnShutdown() {
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
