/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;
import net.aoba.rendering.Renderer2D;

public class WrapPanelComponent extends PanelComponent {
	
	public static final UIProperty<Float> ItemSpacingProperty = new UIProperty<>("ItemSpacing", 4f, false, true);
	public static final UIProperty<Float> RowSpacingProperty = new UIProperty<>("RowSpacing", 4f, false, true);

	private int firstRealized = 0;
	private int lastRealized = -1;

	private ScrollComponent cachedScrollParent;

	public WrapPanelComponent() {
	}

	@Override
	public void setParent(UIElement parent) {
		super.setParent(parent);
		cachedScrollParent = null;
	}

	private ScrollComponent findScrollParent() {
		if (cachedScrollParent != null)
			return cachedScrollParent;

		UIElement p = getParent();
		while (p != null) {
			if (p instanceof ScrollComponent sc) {
				cachedScrollParent = sc;
				return sc;
			}
			p = p.getParent();
		}
		return null;
	}

	@Override
	public Size measure(Size availableSize) {
		List<UIElement> children = getChildren();
		if (children.isEmpty())
			return new Size(availableSize.width(), 0f);

		float availableWidth = availableSize.width();

		float itemSpacing = getProperty(ItemSpacingProperty);
		float rowSpacing = getProperty(RowSpacingProperty);

		if (isVirtualized()) {
			UIElement first = children.get(0);
			first.measureCore(availableSize);
			Size itemSize = first.getPreferredSize();

			float cellWidth = itemSize.width() + itemSpacing;
			float cellHeight = itemSize.height() + rowSpacing;
			if (cellWidth <= 0f || cellHeight <= 0f)
				return new Size(availableWidth, 0f);
			int columns = Math.max(1, (int) Math.floor(availableWidth / cellWidth));
			int totalRows = (int) Math.ceil((float) children.size() / columns);

			int firstVisibleRow = 0;
			int lastVisibleRow = totalRows;

			ScrollComponent scroll = findScrollParent();
			if (scroll != null) {
				float scrollOffset = scroll.getProperty(ScrollComponent.ScrollOffsetProperty);
				float viewportHeight = scroll.getViewportHeight();
				firstVisibleRow = Math.max(0, (int) Math.floor(scrollOffset / cellHeight));
				lastVisibleRow = (int) Math.floor((scrollOffset + viewportHeight) / cellHeight);
			}

			firstRealized = firstVisibleRow * columns;
			lastRealized = Math.min((lastVisibleRow + 1) * columns - 1, children.size() - 1);

			for (int i = firstRealized; i <= lastRealized; i++) {
				UIElement child = children.get(i);
				if (child != null)
					child.measureCore(availableSize);
			}

			return new Size(availableWidth, Math.max(0f, totalRows * cellHeight - rowSpacing));
		} else {
			// Non-virtualized: measure all children
			float x = 0f;
			float rowHeight = 0f;
			float totalHeight = 0f;

			for (UIElement child : children) {
				if (child == null || !child.getProperty(UIElement.IsVisibleProperty))
					continue;

				child.measureCore(availableSize);
				Size childSize = child.getPreferredSize();

				if (x > 0f && x + childSize.width() > availableWidth) {
					totalHeight += rowHeight + rowSpacing;
					x = 0f;
					rowHeight = 0f;
				}

				x += childSize.width() + itemSpacing;
				rowHeight = Math.max(rowHeight, childSize.height());
			}

			totalHeight += rowHeight;
			return new Size(availableWidth, totalHeight);
		}
	}

	@Override
	public void arrange(Rectangle finalSize) {
		setActualSize(finalSize);

		List<UIElement> children = getChildren();
		if (children.isEmpty())
			return;

		float availableWidth = finalSize.width();

		if (isVirtualized()) {
			arrangeVirtualized(finalSize, children, availableWidth);
		} else {
			arrangeStandard(finalSize, children, availableWidth);
		}
	}

	private void arrangeVirtualized(Rectangle finalSize, List<UIElement> children, float availableWidth) {
		UIElement first = children.get(0);
		Size itemSize = first.getPreferredSize();
		float cellWidth = itemSize.width() + getProperty(ItemSpacingProperty);
		float cellHeight = itemSize.height() + getProperty(RowSpacingProperty);
		int columns = Math.max(1, (int) Math.floor(availableWidth / cellWidth));

		int size = children.size();
		int last = Math.min(lastRealized, size - 1);
		for (int i = firstRealized; i <= last; i++) {
			UIElement child = children.get(i);
			if (child == null || !child.getProperty(UIElement.IsVisibleProperty))
				continue;

			int row = i / columns;
			int col = i % columns;
			child.arrange(new Rectangle(finalSize.x() + col * cellWidth, finalSize.y() + row * cellHeight,
					itemSize.width(), itemSize.height()));
		}
	}

	@Override
	public void update() {
		if (!isVirtualized()) {
			super.update();
			return;
		}
		List<UIElement> children = getChildren();
		int last = Math.min(lastRealized, children.size() - 1);
		for (int i = firstRealized; i <= last; i++) {
			UIElement child = children.get(i);
			if (child != null)
				child.update();
		}
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		if (!getProperty(UIElement.IsVisibleProperty))
			return;

		if (isVirtualized()) {
			boolean clip = getProperty(UIElement.ClipToBoundsProperty);
			if (clip)
				renderer.beginClip(actualSize);

			List<UIElement> children = getChildren();
			int last = Math.min(lastRealized, children.size() - 1);
			for (int i = firstRealized; i <= last; i++) {
				UIElement child = children.get(i);
				if (child != null && child.getProperty(UIElement.IsVisibleProperty))
					child.draw(renderer, partialTicks);
			}

			if (clip)
				renderer.endClip();
		}else {
			super.draw(renderer, partialTicks);
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		boolean isHitTestVisible = getProperty(UIElement.IsHitTestVisibleProperty);
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		
		// Return if not visible
		if (!isHitTestVisible || !isVisible)
			return;
		
		if (!isVirtualized()) {
			List<UIElement> children = getChildren();
			int last = Math.min(lastRealized, children.size() - 1);
			for (int i = firstRealized; i <= last; i++) {
				UIElement child = children.get(i);
				if (child != null)
					child.onMouseMove(event);
			}
		}else {
			super.onMouseMove(event);
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		if (isVirtualized()) {
			List<UIElement> children = getChildren();
			int last = Math.min(lastRealized, children.size() - 1);
			for (int i = firstRealized; i <= last; i++) {
				UIElement child = children.get(i);
				if (child != null) {
					child.onMouseClick(event);
					if (event.isCancelled())
						break;
				}
			}
		}else {
			super.onMouseClick(event);
		}
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (isVirtualized()) {
			List<UIElement> children = getChildren();
			int last = Math.min(lastRealized, children.size() - 1);
			for (int i = firstRealized; i <= last; i++) {
				UIElement child = children.get(i);
				if (child != null) {
					child.onMouseScroll(event);
					if (event.isCancelled())
						break;
				}
			}
		}else {
			super.onMouseScroll(event);
		}
	}

	private void arrangeStandard(Rectangle finalSize, List<UIElement> children, float availableWidth) {
		float itemSpacing = getProperty(ItemSpacingProperty);
		float rowSpacing = getProperty(RowSpacingProperty);

		float x = 0f;
		float y = 0f;
		float rowHeight = 0f;

		for (UIElement child : children) {
			if (child == null || !child.getProperty(UIElement.IsVisibleProperty))
				continue;

			Size childSize = child.getPreferredSize();

			if (x > 0f && x + childSize.width() > availableWidth) {
				y += rowHeight + rowSpacing;
				x = 0f;
				rowHeight = 0f;
			}

			child.arrange(new Rectangle(finalSize.x() + x, finalSize.y() + y, childSize.width(), childSize.height()));

			x += childSize.width() + itemSpacing;
			rowHeight = Math.max(rowHeight, childSize.height());
		}
	}
}
