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

public class StackPanelComponent extends PanelComponent {
	public enum StackType {
		Horizontal, Vertical
	}

	protected StackType stackType = StackType.Vertical;
	protected float spacing = 0f;

	public StackPanelComponent() {

    }

	public void setDirection(StackType direction) {
		stackType = direction;
		invalidateMeasure();
	}

	public float getSpacing() {
		return spacing;
	}

	public void setSpacing(float spacing) {
		if (this.spacing != spacing) {
			this.spacing = spacing;
			invalidateMeasure();
		}
	}

	@Override
	public Size measure(Size availableSize) {
		List<UIElement> children = getChildren();
		int visibleCount = 0;

		if (stackType == StackType.Horizontal) {
			float totalWidth = 0;
			float maxHeight = 0;
			for (UIElement element : children) {
				if (element == null || !element.isVisible())
					continue;

				element.measureCore(availableSize);
				Size resultingSize = element.getPreferredSize();
				totalWidth += resultingSize.getWidth();
				maxHeight = Math.max(maxHeight, resultingSize.getHeight());
				visibleCount++;
			}
			if (visibleCount > 1)
				totalWidth += spacing * (visibleCount - 1);
			return new Size(totalWidth, maxHeight);
		} else {
			Size newSize = new Size(availableSize.getWidth(), 0.0f);
			for (UIElement element : children) {
				if (element == null || !element.isVisible())
					continue;

				element.measureCore(availableSize);
				Size resultingSize = element.getPreferredSize();
				newSize.setHeight(newSize.getHeight() + resultingSize.getHeight());
				visibleCount++;
			}
			if (visibleCount > 1)
				newSize.setHeight(newSize.getHeight() + spacing * (visibleCount - 1));
			return newSize;
		}
	}

	@Override
	public void arrange(Rectangle finalSize) {
		if (parent != null) {
			setActualSize(finalSize);
		}

		List<UIElement> children = getChildren();

		if (stackType == StackType.Horizontal) {
			float x = 0;
			for (UIElement element : children) {
				if (element == null || !element.isVisible())
					continue;

				Size preferredSize = element.getPreferredSize();
				element.arrange(new Rectangle(finalSize.getX() + x, finalSize.getY(),
						preferredSize.getWidth(), finalSize.getHeight()));
				x += preferredSize.getWidth() + spacing;
			}
		} else {
			float y = 0;
			for (UIElement element : children) {
				if (element == null || !element.isVisible())
					continue;

				Size preferredSize = element.getPreferredSize();
				element.arrange(new Rectangle(finalSize.getX(), finalSize.getY() + y, finalSize.getWidth(),
						preferredSize.getHeight()));
				y += preferredSize.getHeight() + spacing;
			}
		}
	}
}
