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

package net.aoba.gui.tabs.components;

import net.aoba.gui.IGuiElement;

public class StackPanelComponent extends Component {
	public enum StackType {
		Horizontal, Vertical
	}

	protected StackType stackType = StackType.Vertical;

	public StackPanelComponent(IGuiElement parent) {
		super(parent);
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void OnChildAdded(IGuiElement child) {
		this.RecalculateHeight();
	}

	@Override
	public void OnChildChanged(IGuiElement child) {
		this.RecalculateHeight();
	}
	
	@Override
	public void OnVisibilityChanged() {
		this.RecalculateHeight();
	}

	public void RecalculateHeight() {
		int height = 0;
		for (int i = 0; i < children.size(); i++) {
			Component iChild = children.get(i);

			// If the child is visible, increase the height of the StackPanel.
			if (iChild.isVisible()) {
				height += iChild.getHeight();
			}

			// Move the Top of the child below to the top + height of the previous element.
			if (i + 1 != children.size()) {
				Component childBelow = children.get(i + 1);
				childBelow.setTop(height);
			}
		}
		setHeight(height);
	}
}
