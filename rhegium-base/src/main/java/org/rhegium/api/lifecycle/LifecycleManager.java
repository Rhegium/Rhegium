package org.rhegium.api.lifecycle;

import java.util.Collection;
import java.util.Date;

public interface LifecycleManager {

	void registerLifecycleAware(LifecycleAware lifecycleAware);

	void removeLifecycleAware(LifecycleAware lifecycleAware);

	Collection<LifecycleAware> getLifecycleAwares();

	void initialize() throws Exception;

	void startup() throws Exception;

	void shutdown() throws Exception;

	void shutdown(long timeout) throws Exception;

	String getUptime();

	Date getStartTime();

	String getVersion();

}
