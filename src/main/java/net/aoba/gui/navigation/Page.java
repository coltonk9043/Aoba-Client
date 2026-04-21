/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.gui.UIElement;
import net.aoba.rendering.Renderer2D;

public class Page {
	protected String title;
	protected List<Window> tabs = new ArrayList<Window>();

	private boolean isVisible;

	public Page(String title) {
		this.title = title;
	}

	public void initialize() {
		for (Window tab : tabs) {
			tab.initialize();
		}
	}

	public String getTitle() {
		return title;
	}

	public void addWindow(Window hud) {
		hud.parentPage = this;
		hud.setProperty(UIElement.IsVisibleProperty,isVisible);
		tabs.add(hud);
		if (hud.isInitialized())
			hud.invalidateMeasure();
		else
			hud.initialize();
	}

	public void removeWindow(Window hud) {
		hud.parentPage = null;
		tabs.remove(hud);
	}

	public void setVisible(boolean state) {
		isVisible = state;

		for (Window hud : tabs) {
			hud.setProperty(UIElement.IsVisibleProperty,state);
		}
	}

	public void update() {
		if (isVisible) {
			Iterator<Window> tabIterator = tabs.iterator();
			while (tabIterator.hasNext()) {
				tabIterator.next().update();
			}
		}
	}

	public void render(Renderer2D renderer, float partialTicks) {
		if (isVisible) {
			Iterator<Window> tabIterator = tabs.iterator();
			while (tabIterator.hasNext()) {
				tabIterator.next().draw(renderer, partialTicks);
			}
		}
	}

	public void moveToFront(Window window) {
		if (tabs.size() > 1) {
			Window temp = tabs.get(tabs.size() - 1);
			int indexOfWindow = tabs.indexOf(window);
			tabs.set(indexOfWindow, temp);
			tabs.set(tabs.size() - 1, window);
		}
	}

	public void onMouseMove(MouseMoveEvent event) {
		for (int i = tabs.size() - 1; i >= 0; i--) {
			tabs.get(i).onMouseMove(event);
		}
	}

	public void onMouseClick(MouseClickEvent event) {
		for (int i = tabs.size() - 1; i >= 0; i--) {
			tabs.get(i).onMouseClick(event);
			if (event.isCancelled()) break;
		}
	}

	public void onMouseScroll(MouseScrollEvent event) {
		for (int i = tabs.size() - 1; i >= 0; i--) {
			tabs.get(i).onMouseScroll(event);
			if (event.isCancelled()) break;
		}
	}
}
