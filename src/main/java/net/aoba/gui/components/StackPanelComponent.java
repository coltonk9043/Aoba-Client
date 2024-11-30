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

import java.util.List;

import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.gui.UIElement;

public class StackPanelComponent extends Component {
	public enum StackType {
		Horizontal, Vertical
	}

	protected StackType stackType = StackType.Vertical;

	public StackPanelComponent() {
		super();
	}

	@Override
	public void measure(Size availableSize) {
		Size newSize = new Size(availableSize.getWidth(), 0.0f);
		List<UIElement> children = getChildren();
		if (children.size() > 0) {
			for (UIElement element : children) {
				if (element == null || !element.isVisible())
					continue;

				element.measure(availableSize);
				Size resultingSize = element.getPreferredSize();
				newSize.setHeight(newSize.getHeight() + resultingSize.getHeight());
			}
		}
		preferredSize = newSize;
	}

	@Override
	public void arrange(Rectangle finalSize) {
		if (this.parent != null) {
			setActualSize(finalSize);
		}

		float y = 0;
		List<UIElement> children = getChildren();
		for (UIElement element : children) {
			if (element == null || !element.isVisible())
				continue;

			Size preferredSize = element.getPreferredSize();
			element.arrange(new Rectangle(finalSize.getX(), finalSize.getY() + y, finalSize.getWidth(),
					preferredSize.getHeight()));
			y += preferredSize.getHeight();
		}
	}
}
