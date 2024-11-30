/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.Iterator;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.UIElement;

public abstract class Component extends UIElement {

	public Component() {
		super();
		this.margin = new Margin();
	}

	@Override
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		hovered = false;
	}

	/**
	 * Triggers when the mouse is moved.
	 *
	 * @param mouseMoveEvent Event fired.
	 */
	public void onMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (isHitTestVisible()) {
			// Propagate to children.
			Iterator<UIElement> tabIterator = getChildren().iterator();
			while (tabIterator.hasNext()) {
				tabIterator.next().onMouseMove(mouseMoveEvent);
			}

			boolean wasHovered = hovered;
			if (mouseMoveEvent.isCancelled() || !visible || !Aoba.getInstance().guiManager.isClickGuiOpen()) {
				this.hovered = false;
				if (wasHovered) {
					GuiManager.setTooltip(null);
				}
			} else {

				float mouseX = (float) mouseMoveEvent.getX();
				float mouseY = (float) mouseMoveEvent.getY();

				this.hovered = actualSize.intersects(mouseX, mouseY);

				String tooltip = getTooltip();
				if (hovered && tooltip != null) {
					GuiManager.setTooltip(tooltip);
					mouseMoveEvent.cancel();
				} else if (wasHovered) {
					GuiManager.setTooltip(null);
				}
			}
		}

	}

	public void onMouseClick(MouseClickEvent event) {
		// Propagate to children.
		Iterator<UIElement> tabIterator = getChildren().iterator();
		while (tabIterator.hasNext()) {
			tabIterator.next().onMouseClick(event);
		}
	}

	public String getTooltip() {
		return null;
	}
}
