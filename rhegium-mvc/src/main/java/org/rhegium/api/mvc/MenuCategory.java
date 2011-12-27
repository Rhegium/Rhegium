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
package org.rhegium.api.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MenuCategory<C> {

	private final List<MenuEntry<?, ?, ?>> menuEntries = new ArrayList<MenuEntry<?, ?, ?>>();
	private final C content;
	private final String title;

	public MenuCategory(C content, String title) {
		this.content = content;
		this.title = title;
	}

	public C getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public void addMenuEntry(MenuEntry<?, ?, ?> menuEntry) {
		menuEntries.add(menuEntry);
	}

	public Collection<MenuEntry<?, ?, ?>> getMenuEntries() {
		return Collections.unmodifiableCollection(menuEntries);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuCategory<?> other = (MenuCategory<?>) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		return true;
	}

}
