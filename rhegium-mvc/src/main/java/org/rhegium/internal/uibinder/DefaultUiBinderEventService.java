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
package org.rhegium.internal.uibinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;
import org.rhegium.api.uibinder.EventSubscriber;
import org.rhegium.api.uibinder.UiBinderEventService;

class DefaultUiBinderEventService implements UiBinderEventService {

	private final Map<String, List<Dispatcher>> dispatcherMapping = new HashMap<String, List<Dispatcher>>();

	@Override
	public void registerComponentController(Controller<?, ?, ?> componentController) {
		for (Method method : componentController.getClass().getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())
					|| !method.isAnnotationPresent(EventSubscriber.class)) {

				continue;
			}

			EventSubscriber subscriber = method.getAnnotation(EventSubscriber.class);
			String eventName = subscriber.value();

			List<Dispatcher> dispatchers = dispatcherMapping.get(eventName);
			if (dispatchers == null) {
				dispatchers = new ArrayList<Dispatcher>();
				dispatcherMapping.put(eventName, dispatchers);
			}

			dispatchers.add(new Dispatcher(componentController, method));
		}
	}

	@Override
	public void dispatchEvent(View<?, ?, ?> view, String eventName, Object... arguments) {
		List<Dispatcher> dispatchers = dispatcherMapping.get(eventName);
		if (dispatchers == null || dispatchers.size() == 0) {
			return;
		}

		Controller<?, ?, ?> componentController = view.getComponentController();
		for (Dispatcher dispatcher : dispatchers) {
			if (dispatcher.getComponentController() == componentController) {
				Method method = dispatcher.getMethod();
				try {
					method.invoke(componentController, arguments);
				}
				catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
				catch (IllegalArgumentException e) {
					throw new IllegalStateException(e);
				}
				catch (InvocationTargetException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	private class Dispatcher {

		private final Controller<?, ?, ?> componentController;
		private final Method method;

		Dispatcher(Controller<?, ?, ?> componentController, Method method) {
			this.componentController = componentController;
			this.method = method;
		}

		public Controller<?, ?, ?> getComponentController() {
			return componentController;
		}

		public Method getMethod() {
			return method;
		}
	}

}
