package org.rhegium.api.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.ui.VerticalLayout;

public class MenuCategory {

	private final List<MenuEntry<?, ?>> menuEntries = new ArrayList<MenuEntry<?, ?>>();
	private final VerticalLayout content;
	private final String title;

	public MenuCategory(VerticalLayout content, String title) {
		this.content = content;
		this.title = title;
	}

	public VerticalLayout getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public void addMenuEntry(MenuEntry<?, ?> menuEntry) {
		menuEntries.add(menuEntry);
	}

	public Collection<MenuEntry<?, ?>> getMenuEntries() {
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
		MenuCategory other = (MenuCategory) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		return true;
	}

}
