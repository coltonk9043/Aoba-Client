/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.gui.colors.Colors;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class UIElement {
	public static boolean DEBUG = false;

	protected static MinecraftClient MC = MinecraftClient.getInstance();
	protected static AobaClient AOBA = Aoba.getInstance();

	private final ArrayList<UIElement> children = new ArrayList<UIElement>();
	protected UIElement parent;

	// Constraints (Dimensions will always adhere to these if it is not null)
	protected Float width = null;
	protected Float height = null;
	protected Float maxWidth = null;
	protected Float maxHeight = null;
	protected Float minWidth = null;
	protected Float minHeight = null;
	protected boolean initialized = false;

	// Spacing
	protected Margin margin;

	// Actual physical size on the screen.
	protected Size preferredSize;
	protected Rectangle actualSize;
	protected String tooltip = null;
	protected boolean visible = true;
	protected boolean hovered = false;
	protected boolean isHitTestVisible = true;

	public UIElement() {
		preferredSize = new Size(0.0f, 0.0f);
		actualSize = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void initialize() {
		boolean wasInitialized = initialized;
		if (!wasInitialized) {
			initialized = true;
		}

		for (UIElement child : children) {
			if (child == null)
				continue;
			child.initialize();
		}

		if (!wasInitialized) {
			onInitialized();
			invalidateMeasure();
		}
	}

	public boolean isInitialized() {
		return initialized;
	}

	protected void onInitialized() {

	}

	public void update() {
		for (UIElement child : children) {
			child.update();
		}
	}

	/**
	 * Abstract method for drawing components onto the screen.
	 *
	 * @param drawContext  DrawContext of the game.
	 * @param partialTicks Partial Ticks of the game.
	 */
	public void draw(DrawContext drawContext, float partialTicks) {
		if (DEBUG) {
			Render2D.drawBoxOutline(drawContext, actualSize, Colors.Red);
		}

		if (isVisible()) {
			for (UIElement child : children) {
				if (child.visible) {
					child.draw(drawContext, partialTicks);
				}
			}
		}
	}

	public Size getPreferredSize() {
		return preferredSize;
	}

	public Rectangle getActualSize() {
		return actualSize;
	}

	protected void setActualSize(Rectangle actualSize) {
		this.actualSize = actualSize;
	}

	public Margin getMargin() {
		return margin;
	}

	public void setMargin(Margin margin) {
		if (!this.margin.equals(margin)) {
			this.margin = margin;
			invalidateMeasure();
		}
	}

	public Float getWidth() {
		return width;
	}

	public void setSize(Size size) {
		Float newWidth = size.getWidth();
		Float newHeight = size.getHeight();

		if (width != newWidth || height != newHeight) {
			width = size.getWidth();
			height = size.getHeight();
			invalidateMeasure();
		}
	}

	public void setSize(Float width, Float height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			invalidateMeasure();
		}
	}

	public void setWidth(Float width) {
		if (this.width != width) {
			this.width = width;
			invalidateMeasure();
		}
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		if (this.height != height) {
			this.height = height;
			invalidateMeasure();
		}
	}

	public Float getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(Float minWidth) {
		this.minWidth = minWidth;
	}

	public Float getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(Float minHeight) {
		this.minHeight = minHeight;
	}

	public Float getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Float maxWidth) {
		this.maxWidth = maxWidth;
	}

	public Float getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(Float maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * Whether or not the component is currently visible.
	 *
	 * @return Visibility state as a boolean.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets whether the component is visible or not
	 *
	 * @param bool State to set visibility to.
	 */
	public void setVisible(boolean bool) {
		if (visible == bool)
			return;

		visible = bool;
		hovered = false;

		// If parent is not null, notify the parent.
		if (parent != null) {
			parent.onChildChanged(this);
		}

		onVisibilityChanged();
	}

	public boolean isHitTestVisible() {
		return isHitTestVisible;
	}

	public void setIsHitTestVisible(boolean state) {
		isHitTestVisible = state;
	}

	/**
	 * Returns the parent of the Component.
	 *
	 * @return Parent of the component as a ClickGuiTab.
	 */
	public UIElement getParent() {
		return parent;
	}

	public void setParent(UIElement parent) {
		this.parent = parent;
		invalidateMeasure();
	}

	public void invalidateMeasure() {
		if (initialized) {
			if (parent != null) {
				parent.invalidateMeasure();
			} else {
				// Construct new bounds based off of width/height constraints.
				Size size;
				if (parent == null)
					size = new Size(0f, 0f);
				else
					size = parent.getPreferredSize();

				if (width != null)
					size.setWidth(width);

				if (height != null)
					size.setHeight(height);

				if (minWidth != null && size.getWidth() < minWidth)
					size.setWidth(minWidth);

				if (minHeight != null && size.getHeight() < minHeight)
					size.setHeight(minHeight);

				if (maxWidth != null && size.getWidth() > maxWidth)
					size.setWidth(maxWidth);

				if (maxHeight != null && size.getHeight() > maxHeight)
					size.setHeight(maxHeight);

				measure(size);
				Rectangle rect = new Rectangle(0f, 0f, preferredSize.getWidth(), preferredSize.getHeight());
				arrange(rect);
			}
		}
	}

	public void invalidateArrange() {
		if (initialized) {
			Rectangle rect = new Rectangle(0f, 0f, preferredSize.getWidth(), preferredSize.getHeight());
			arrange(rect);
		}
	}

	protected Size getStartingSize(Size availableSize) {
		return new Size(0f, 0f);
	}

	/**
	 * Measures the UI element accounting for all of the children. This method spans
	 * all of the children, starting from the bottom up.
	 * 
	 * @param availableSize The total amount of space that the UI element has to fit
	 *                      in.
	 * @return The new preferred size of the UI Element.
	 */
	public void measure(Size availableSize) {
		if (!isVisible()) {
			return;
		}

		if (initialized) {
			Size finalSize = getStartingSize(availableSize);

			for (UIElement element : children) {
				if (!element.visible)
					continue;

				element.measure(availableSize);
				Size resultingSize = element.getPreferredSize();

				if (resultingSize.getWidth() > finalSize.getWidth())
					finalSize.setWidth(resultingSize.getWidth());

				if (resultingSize.getHeight() > finalSize.getHeight())
					finalSize.setHeight(resultingSize.getHeight());
			}

			if (margin != null) {

				Float marginLeft = margin.getLeft();
				Float marginTop = margin.getTop();
				Float marginRight = margin.getRight();
				Float marginBottom = margin.getBottom();

				if (marginLeft != null)
					finalSize.setWidth(finalSize.getWidth() + marginLeft);

				if (marginRight != null)
					finalSize.setWidth(finalSize.getWidth() + marginRight);

				if (marginTop != null)
					finalSize.setHeight(finalSize.getHeight() + marginTop);

				if (marginBottom != null)
					finalSize.setHeight(finalSize.getHeight() + marginBottom);
			}

			if (minWidth != null && finalSize.getWidth() < minWidth) {
				finalSize.setWidth(minWidth);
			} else if (maxWidth != null && finalSize.getWidth() > maxWidth) {
				finalSize.setWidth(maxWidth);
			}

			if (minHeight != null && finalSize.getHeight() < minHeight) {
				finalSize.setHeight(minHeight);
			} else if (maxHeight != null && finalSize.getHeight() > maxHeight) {
				finalSize.setHeight(maxHeight);
			}

			preferredSize = finalSize;
		}
	}

	public void arrange(Rectangle finalSize) {
		if (initialized) {
			Rectangle newFinalSize;
			if (margin != null) {
				newFinalSize = new Rectangle(finalSize);

				Float marginLeft = margin.getLeft();
				Float marginTop = margin.getTop();
				Float marginRight = margin.getRight();
				Float marginBottom = margin.getBottom();

				// Left Margin
				if (marginLeft != null) {
					newFinalSize.setX(newFinalSize.getX() + marginLeft);
					newFinalSize.setWidth(newFinalSize.getWidth() - marginLeft);
				}
				// Top Margin
				if (marginTop != null) {
					newFinalSize.setY(newFinalSize.getY() + marginTop);
					newFinalSize.setHeight(newFinalSize.getHeight() - marginTop);
				}
				// Right Margin
				if (marginRight != null) {
					newFinalSize.setWidth(newFinalSize.getWidth() - marginRight);
				}

				// Bottom Margin
				if (marginBottom != null) {
					newFinalSize.setHeight(newFinalSize.getHeight() - marginBottom);
				}
			} else {
				newFinalSize = finalSize;
			}

			Rectangle oldActualSize = actualSize;
			setActualSize(newFinalSize);

			if (!oldActualSize.equals(actualSize)) {
				for (UIElement element : children) {
					element.arrange(getActualSize());
				}
			}
		}
	}

	public List<UIElement> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public void addChild(UIElement child) {
		if (child == null)
			return;

		if (initialized && !child.initialized)
			child.initialize();

		child.setParent(this);
		children.add(child);
		onChildAdded(child);
	}

	public void removeChild(UIElement child) {
		if (child == null)
			return;

		children.remove(child);
		onChildRemoved(child);
	}

	public void clearChildren() {
		children.clear();
		// TODO: Implement children cleared as list.
	}

	public void onChildAdded(UIElement child) {
		invalidateMeasure();
	}

	public void onChildChanged(UIElement child) {
		invalidateMeasure();
	}

	public void onChildRemoved(UIElement child) {
		invalidateMeasure();
	}

	public void onVisibilityChanged() {
		invalidateMeasure();
	}

	/**
	 * Dispose method to release resources.
	 */
	public void dispose() {
		Iterator<UIElement> children = getChildren().iterator();
		while (children.hasNext()) {
			children.next().dispose();
		}

		clearChildren();
	}

	public void onMouseMove(MouseMoveEvent event) {
		boolean wasHovered = hovered;
		if (isHitTestVisible() && isVisible()) {
			// Propagate to children.
			Iterator<UIElement> tabIterator = getChildren().iterator();
			while (tabIterator.hasNext()) {
				tabIterator.next().onMouseMove(event);
			}

			if (event.isCancelled()) {
				hovered = false;
				if (wasHovered) {
					GuiManager.setTooltip(null);
				}
			} else {
				float mouseX = (float) event.getX();
				float mouseY = (float) event.getY();

				hovered = actualSize.intersects(mouseX, mouseY);

				if (!event.isCancelled() && hovered) {
					event.cancel();
					String tooltip = getTooltip();
					GuiManager.setTooltip(tooltip);
				} else if (wasHovered) {
					GuiManager.setTooltip(null);
				}
			}
		} else {
			hovered = false;
			if (wasHovered) {
				GuiManager.setTooltip(null);
			}
		}
	}

	public void onMouseClick(MouseClickEvent event) {
		// Propagate to children.
		Iterator<UIElement> tabIterator = getChildren().iterator();
		while (tabIterator.hasNext()) {
			tabIterator.next().onMouseClick(event);
			if (event.isCancelled())
				break;
		}
	}

	public void onMouseScroll(MouseScrollEvent event) {
		// Propagate to children.
		Iterator<UIElement> tabIterator = getChildren().iterator();
		while (tabIterator.hasNext()) {
			tabIterator.next().onMouseScroll(event);
			if (event.isCancelled())
				break;
		}
	}

	public String getTooltip() {

		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}
