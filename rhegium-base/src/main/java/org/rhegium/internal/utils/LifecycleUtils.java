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
package org.rhegium.internal.utils;

import org.rhegium.api.lifecycle.LifecycleAware;
import org.rhegium.api.lifecycle.LifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public final class LifecycleUtils {

	private static final Logger LOG = LoggerFactory.getLogger(LifecycleUtils.class);

	private LifecycleUtils() {
	}

	public static <T> T startLifecycleEntity(T entity, Injector injector) throws Exception {
		injector.injectMembers(entity);

		if (isLifecycleAware(entity)) {
			LifecycleManager lifecycleManager = injector.getInstance(LifecycleManager.class);
			lifecycleManager.registerLifecycleAware((LifecycleAware) entity);
		}

		return startLifecycleEntity(entity);
	}

	public static <T> T startLifecycleEntity(T entity) throws Exception {
		notifyInitialized(entity);
		notifyStartup(entity);
		return entity;
	}

	public static <T> void stopLifecycleEntity(T entity) {
		notifyShutdown(entity);
	}

	private static void notifyInitialized(Object value) throws Exception {
		if (isLifecycleAware(value)) {
			((LifecycleAware) value).initialized();
		}
	}

	private static void notifyStartup(Object value) throws Exception {
		if (isLifecycleAware(value)) {
			((LifecycleAware) value).start();
		}
	}

	private static void notifyShutdown(Object value) {
		if (isLifecycleAware(value)) {
			try {
				((LifecycleAware) value).shutdown();
			}
			catch (Exception e) {
				LOG.error("LifecycleAware.shutdown failed", e);
			}
		}
	}

	private static boolean isLifecycleAware(Object value) {
		return LifecycleAware.class.isAssignableFrom(value.getClass());
	}

}
