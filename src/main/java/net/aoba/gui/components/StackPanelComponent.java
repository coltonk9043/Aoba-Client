/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;

import net.aoba.gui.UIElement;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;

public class StackPanelComponent extends PanelComponent {
	public enum StackType {
		Horizontal, Vertical
	}

	protected StackType stackType = StackType.Vertical;
	protected float spacing = 0f;
	protected boolean lastChildFill = false;

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

	public boolean getLastChildFill() {
		return lastChildFill;
	}

	public void setLastChildFill(boolean lastChildFills) {
		if (this.lastChildFill != lastChildFills) {
			this.lastChildFill = lastChildFills;
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
				if (element == null || !element.getProperty(UIElement.IsVisibleProperty))
					continue;

				element.measureCore(availableSize);
				Size resultingSize = element.getPreferredSize();
				totalWidth += resultingSize.width();
				maxHeight = Math.max(maxHeight, resultingSize.height());
				visibleCount++;
			}
			if (visibleCount > 1)
				totalWidth += spacing * (visibleCount - 1);
			return new Size(totalWidth, maxHeight);
		} else {
			float totalHeight = 0.0f;
			for (UIElement element : children) {
				if (element == null || !element.getProperty(UIElement.IsVisibleProperty))
					continue;

				element.measureCore(availableSize);
				Size resultingSize = element.getPreferredSize();
				totalHeight += resultingSize.height();
				visibleCount++;
			}
			if (visibleCount > 1)
				totalHeight += spacing * (visibleCount - 1);
			return new Size(availableSize.width(), totalHeight);
		}
	}

	@Override
	public void arrange(Rectangle finalSize) {
		setActualSize(finalSize);

		List<UIElement> children = getChildren();

		// If we enabled last child fill, find the last
		// VISIBLE child.
		UIElement lastVisible = null;
		if (lastChildFill) {
			for (int i = children.size() - 1; i >= 0; i--) {
				UIElement element = children.get(i);
				if (element != null && element.getProperty(UIElement.IsVisibleProperty)) {
					lastVisible = element;
					break;
				}
			}
		}

		// Stack the elements in the proper orientation.
		if (stackType == StackType.Horizontal) {
			float x = 0;
			for (UIElement element : children) {
				if (element == null || !element.getProperty(UIElement.IsVisibleProperty))
					continue;

				if (element == lastVisible) {
					float remainingWidth = finalSize.width() - x;
					element.arrange(new Rectangle(finalSize.x() + x, finalSize.y(),
							remainingWidth, finalSize.height()));
				} else {
					Size preferredSize = element.getPreferredSize();
					element.arrange(new Rectangle(finalSize.x() + x, finalSize.y(),
							preferredSize.width(), finalSize.height()));
					x += preferredSize.width() + spacing;
				}
			}
		} else {
			float y = 0;
			for (UIElement element : children) {
				if (element == null || !element.getProperty(UIElement.IsVisibleProperty))
					continue;

				if (element == lastVisible) {
					float remainingHeight = finalSize.height() - y;
					element.arrange(new Rectangle(finalSize.x(), finalSize.y() + y,
							finalSize.width(), remainingHeight));
				} else {
					Size preferredSize = element.getPreferredSize();
					element.arrange(new Rectangle(finalSize.x(), finalSize.y() + y, finalSize.width(),
							preferredSize.height()));
					y += preferredSize.height() + spacing;
				}
			}
		}
	}
}
