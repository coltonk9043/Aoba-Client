/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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
		if (parent != null) {
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
