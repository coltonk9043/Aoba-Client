/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.Size;
import net.aoba.gui.UIElement;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.GuiGraphics;

// TODO: In the future i would like to take another approach to this...
// The ItemsComponent should somehow remove itself from the component tree such
// that a scroll component will WRAP its items.
// Examples of this can be seen in the HTML DOM or WPF logical/visual tree.
public class ScrollComponent extends PanelComponent implements MouseScrollListener {
	private static final float SCROLLBAR_WIDTH = 10f;
	private static final float SCROLLBAR_PADDING = 2f;

	protected int visibleElements = 5;
	protected float spacing = 0f;

	private float scrollOffset = 0f;
	private float totalChildrenHeight = 0f;
	private boolean draggingScrollbar = false;
	private boolean hoveringThumb = false;
	private float dragStartY = 0f;
	private float dragStartOffset = 0f;

	public ScrollComponent() {
	}

	public int getVisibleElements() {
		return visibleElements;
	}

	public void setVisibleElements(int visibleElements) {
		if (this.visibleElements != visibleElements) {
			this.visibleElements = visibleElements;
			invalidateMeasure();
		}
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
	public void onVisibilityChanged() {
		super.onVisibilityChanged();
		if (isVisible())
			Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
		else
			Aoba.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
	}

	@Override
	public Size measure(Size availableSize) {
		List<UIElement> children = getChildren();

		// Measure all children and calculate both total and visible heights.
		float totalHeight = 0f;
		float visibleHeight = 0f;
		int visibleCount = 0;
		for (UIElement element : children) {
			if (element == null || !element.isVisible())
				continue;

			element.measureCore(availableSize);
			float h = element.getPreferredSize().getHeight();
			totalHeight += h;
			if (visibleCount < visibleElements)
				visibleHeight += h;
			visibleCount++;
		}

		if (visibleCount > 1)
			totalHeight += spacing * (visibleCount - 1);
		if (Math.min(visibleCount, visibleElements) > 1)
			visibleHeight += spacing * (Math.min(visibleCount, visibleElements) - 1);

		// If scrollbar is needed, re-measure with reduced width.
		if (totalHeight > visibleHeight && visibleCount > visibleElements) {
			float reducedWidth = availableSize.getWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING;
			Size reducedSize = new Size(reducedWidth, availableSize.getHeight());

			totalHeight = 0f;
			visibleHeight = 0f;
			int counted = 0;
			for (UIElement element : children) {
				if (element == null || !element.isVisible())
					continue;

				element.measureCore(reducedSize);
				float h = element.getPreferredSize().getHeight();
				totalHeight += h;
				if (counted < visibleElements)
					visibleHeight += h;
				counted++;
			}

			if (counted > 1)
				totalHeight += spacing * (counted - 1);
			if (Math.min(counted, visibleElements) > 1)
				visibleHeight += spacing * (Math.min(counted, visibleElements) - 1);
		}

		totalChildrenHeight = totalHeight;

		return new Size(availableSize.getWidth(), visibleHeight);
	}

	@Override
	public void arrange(Rectangle finalSize) {
		if (parent != null) {
			setActualSize(finalSize);
		}

		List<UIElement> children = getChildren();

		// Clamp the scroll offset to the height.
		float finalHeight = finalSize.getHeight();
		float maxScroll = Math.max(0f, totalChildrenHeight - finalHeight);
		scrollOffset = Math.max(0f, Math.min(scrollOffset, maxScroll));

		// Add scroll bar width if the content overflows.
		boolean scrollable = totalChildrenHeight > finalHeight;
		float contentWidth = scrollable
				? finalSize.getWidth() - SCROLLBAR_WIDTH - SCROLLBAR_PADDING
				: finalSize.getWidth();

		// Layout all children by an offset.
		float y = -scrollOffset;
		for (UIElement element : children) {
			if (element == null || !element.isVisible())
				continue;

			Size preferredSize = element.getPreferredSize();
			element.arrange(new Rectangle(
					finalSize.getX(),
					finalSize.getY() + y,
					contentWidth,
					preferredSize.getHeight()));
			y += preferredSize.getHeight() + spacing;
		}
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		if (!isVisible() || !actualSize.isDrawable())
			return;

		float x = actualSize.getX();
		float y = actualSize.getY();
		float w = actualSize.getWidth();
		float h = actualSize.getHeight();

		// Clip children outside the viewport.
		Render2D.beginClip(actualSize);

		for (UIElement child : getChildren()) {
			if (child.isVisible()) {
				child.draw(drawContext, partialTicks);
			}
		}

		Render2D.endClip();

		// Draw the scrollbar
		// TODO: I want this to be a RectangleComponent in the future.
		// But we are not there yet.
		float viewportHeight = h;
		if (totalChildrenHeight > viewportHeight) {
			float trackX = x + w - SCROLLBAR_WIDTH;
			float trackY = y;
			float trackWidth = SCROLLBAR_WIDTH;
			float trackHeight = h;

			Render2D.drawBox(drawContext, trackX, trackY, trackWidth, trackHeight, new Color(0, 0, 0, 80));

			// Calculate thumb bounds and draw
			float viewportRatio = h / totalChildrenHeight;
			float thumbHeight = Math.max(20f, trackHeight * viewportRatio);
			float maxScroll = totalChildrenHeight - h;
			float scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0f;
			float thumbY = trackY + scrollRatio * (trackHeight - thumbHeight);
			
			Color thumbColor = GuiManager.foregroundColor.getValue();
			if (hoveringThumb || draggingScrollbar) {
				thumbColor = thumbColor.add(55, 55, 55);
			}
			
			Render2D.drawRoundedBox(drawContext, trackX + 1f, thumbY, trackWidth - 2f, thumbHeight, GuiManager.roundingRadius.getValue(), thumbColor);
		}
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (Aoba.getInstance().guiManager.isClickGuiOpen() && hovered) {
			float viewportHeight = actualSize.getHeight();
			if (totalChildrenHeight <= viewportHeight)
				return;

			float scrollAmount = 20f;
			if (event.GetVertical() > 0) {
				scrollOffset = Math.max(0f, scrollOffset - scrollAmount);
			} else if (event.GetVertical() < 0) {
				float maxScroll = totalChildrenHeight - viewportHeight;
				scrollOffset = Math.min(maxScroll, scrollOffset + scrollAmount);
			}

			invalidateArrange();
			event.cancel();
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		if (!isVisible() || !isHitTestVisible())
			return;

		float mouseX = (float) event.mouseX;
		float mouseY = (float) event.mouseY;

		// Check if clicking on the scrollbar track.
		if (totalChildrenHeight > actualSize.getHeight() && event.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			float trackX = actualSize.getX() + actualSize.getWidth() - SCROLLBAR_WIDTH;
			float trackY = actualSize.getY();
			float trackHeight = actualSize.getHeight();

			if (mouseX >= trackX && mouseX <= trackX + SCROLLBAR_WIDTH
					&& mouseY >= trackY && mouseY <= trackY + trackHeight) {

				if (event.action == GLFW.GLFW_PRESS) {
					// Calculate thumb bounds for hit testing.
					float viewportRatio = actualSize.getHeight() / totalChildrenHeight;
					float thumbHeight = Math.max(20f, trackHeight * viewportRatio);
					float maxScroll = totalChildrenHeight - actualSize.getHeight();
					float scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0f;
					float thumbY = trackY + scrollRatio * (trackHeight - thumbHeight);

					if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
						// Start dragging the thumb.
						draggingScrollbar = true;
						dragStartY = mouseY;
						dragStartOffset = scrollOffset;
					} else {
						// Jump to position on track.
						float clickRatio = (mouseY - trackY - thumbHeight / 2f) / (trackHeight - thumbHeight);
						clickRatio = Math.max(0f, Math.min(1f, clickRatio));
						scrollOffset = clickRatio * maxScroll;
						invalidateArrange();
					}

					event.cancel();
					return;
				} else if (event.action == GLFW.GLFW_RELEASE) {
					draggingScrollbar = false;
				}
			}
		}

		if (event.action == GLFW.GLFW_RELEASE && draggingScrollbar) {
			draggingScrollbar = false;
		}

		super.onMouseClick(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);

		// Update thumb hover state.
		if (totalChildrenHeight > 0 && actualSize.isDrawable() && totalChildrenHeight > actualSize.getHeight()) {
			float trackX = actualSize.getX() + actualSize.getWidth() - SCROLLBAR_WIDTH;
			float trackY = actualSize.getY();
			float trackHeight = actualSize.getHeight();
			float viewportRatio = actualSize.getHeight() / totalChildrenHeight;
			float thumbHeight = Math.max(20f, trackHeight * viewportRatio);
			float maxScroll = totalChildrenHeight - actualSize.getHeight();
			float scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0f;
			float thumbY = trackY + scrollRatio * (trackHeight - thumbHeight);

			float mx = (float) event.getX();
			float my = (float) event.getY();
			hoveringThumb = mx >= trackX && mx <= trackX + SCROLLBAR_WIDTH
					&& my >= thumbY && my <= thumbY + thumbHeight;
		} else {
			hoveringThumb = false;
		}

		if (draggingScrollbar) {
			float mouseY = (float) event.getY();
			float trackHeight = actualSize.getHeight();
			float viewportRatio = actualSize.getHeight() / totalChildrenHeight;
			float thumbHeight = Math.max(20f, trackHeight * viewportRatio);
			float maxScroll = totalChildrenHeight - actualSize.getHeight();

			float deltaY = mouseY - dragStartY;
			float scrollableTrack = trackHeight - thumbHeight;
			if (scrollableTrack > 0) {
				float newOffset = dragStartOffset + (deltaY / scrollableTrack) * maxScroll;
				scrollOffset = Math.max(0f, Math.min(maxScroll, newOffset));
				invalidateArrange();
			}
		}
	}
}
