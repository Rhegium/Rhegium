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
package org.rhegium.vaadin.api.mvc;

import org.rhegium.api.mvc.AbstractBindableView;
import org.rhegium.api.mvc.Controller;
import org.rhegium.api.mvc.View;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractVaadinBindableView<CC extends Controller<AbstractComponent, CC, V>, V extends View<AbstractComponent, CC, V>>
		extends AbstractBindableView<AbstractComponent, CC, V> {

	private VerticalLayout layout;

	@Override
	public synchronized final AbstractComponent getComponent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.setSizeFull();
			layout.setMargin(true);
			layout.addComponent(buildComponent());
		}

		return layout;
	}

	@Override
	protected AbstractComponent buildComponent() {
		return new VerticalLayout();
	}

}
