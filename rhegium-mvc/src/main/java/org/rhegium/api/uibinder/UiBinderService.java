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
package org.rhegium.api.uibinder;

import java.util.Locale;

import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;

public interface UiBinderService<C> {

	<CC extends Controller<C, CC, V>, V extends View<C, CC, V>> V bindView(V view, Locale locale);

	<CC extends Controller<C, CC, V>, V extends View<C, CC, V>> V bindView(V view, String xml, Locale locale);

	C bind(Class<? extends C> componentClass, View<C, ?, ?> view, Locale locale);

	C bind(String componentClass, View<C, ?, ?> view, Locale locale);

	boolean isBindable(String componentName);

	boolean isBindable(Class<?> componentClass);

}
