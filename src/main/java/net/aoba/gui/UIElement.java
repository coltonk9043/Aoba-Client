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
import java.util.function.Consumer;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.gui.colors.Colors;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class UIElement {
	public static boolean DEBUG = false;

	protected static Minecraft MC = Minecraft.getInstance();
	protected static AobaClient AOBA = Aoba.getInstance();

	private final ArrayList<UIElement> children = new ArrayList<UIElement>();
	protected UIElement parent;

	private boolean measureDirty = true;
	private Float lastMeasureWidth = null;
	private Float lastMeasureHeight = null;
	protected Float width = null;
	protected Float height = null;
	protected Float maxWidth = null;
	protected Float maxHeight = null;
	protected Float minWidth = null;
	protected Float minHeight = null;
	protected boolean initialized = false;

	// Spacing
	protected Thickness margin;
	protected Thickness padding;

	// Alignment
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.Stretch;
	protected VerticalAlignment verticalAlignment = VerticalAlignment.Stretch;

	// Actual physical size on the screen.
	protected Size preferredSize;
	protected Rectangle actualSize;
	protected String tooltip = null;
	protected boolean visible = true;
	protected boolean hovered = false;
	protected boolean isHitTestVisible = true;

	// Events
	private Consumer<MouseClickEvent> onClicked;

	public UIElement() {
		preferredSize = new Size(0.0f, 0.0f);
		actualSize = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
	}

	/**
	 * Initializes the UI element.
	 */
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

	/**
	 * Returns whether the UI element is initialized.
	 * @return True when initialized, false otherwise.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Fires when the UI element is initialized.
	 */
	protected void onInitialized() {

	}

	/**
	 * Updates the UI element per tick.
	 */
	public void update() {
		for (UIElement child : children) {
			child.update();
		}
	}

	/**
	 * Draws the UI element on the screen.
	 * @param drawContext  DrawContext of the game.
	 * @param partialTicks Partial Ticks of the game.
	 */
	public void draw(GuiGraphics drawContext, float partialTicks) {
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

	/**
	 * Gets the preferred size of the UI element.
	 * @return Preferred size of the UI element.
	 */
	public Size getPreferredSize() {
		return preferredSize;
	}

	/**
	 * Gets the actual size of the UI element.
	 * @return Actual size of the UI element.
	 */
	public Rectangle getActualSize() {
		return actualSize;
	}

	/**
	 * Gets the content area of the UI element (actualSize inset by padding).
	 * This is the area available to children.
	 * @return Content area as a Rectangle.
	 */
	public Rectangle getContentArea() {
		Rectangle area = new Rectangle(getActualSize());
		if (padding != null) {
			if (padding.left() != null) {
				area.setX(area.getX() + padding.left());
				area.setWidth(area.getWidth() - padding.left());
			}
			if (padding.top() != null) {
				area.setY(area.getY() + padding.top());
				area.setHeight(area.getHeight() - padding.top());
			}
			if (padding.right() != null) {
				area.setWidth(area.getWidth() - padding.right());
			}
			if (padding.bottom() != null) {
				area.setHeight(area.getHeight() - padding.bottom());
			}
		}
		return area;
	}

	/**
	 * Sets the actual size of the UI element.
	 * @param actualSize Size to set the UI element to.
	 */
	protected void setActualSize(Rectangle actualSize) {
		this.actualSize = actualSize;
	}

	/**
	 * Gets the margin of the UI element.
	 * @return Margin of the UI element.
	 */
	public Thickness getMargin() {
		return margin;
	}

	/**
	 * Sets the margin of the UI element.
	 * @param val Margin to set to.
	 */
	public void setMargin(Thickness val) {
		if (margin == null || !margin.equals(val)) {
			this.margin = val;
			invalidateMeasure();
		}
	}

	/**
	 * Gets the padding of the UI element.
	 * @return Padding of the UI element.
	 */
	public Thickness getPadding() {
		return padding;
	}

	/**
	 * Sets the padding of the UI element.
	 * @param val Padding to set to.
	 */
	public void setPadding(Thickness val) {
		if (padding == null || !padding.equals(val)) {
			this.padding = val;
			invalidateMeasure();
		}
	}

	/**
	 * Gets the horizontal alignment of the UI element.
	 * @return Horizontal alignment of the UI element.
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * Sets the horizontal alignment of the UI element.
	 * @param alignment Horizontal alignment to set.
	 */
	public void setHorizontalAlignment(HorizontalAlignment alignment) {
		if (this.horizontalAlignment != alignment) {
			this.horizontalAlignment = alignment;
			invalidateMeasure();
		}
	}

	/**
	 * Gets the vertical alignment of the UI element.
	 * @return Vertical alignment of the UI element.
	 */
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * Sets the vertical alignment of the UI element.
	 * @param alignment Vertical alignment to set.
	 */
	public void setVerticalAlignment(VerticalAlignment alignment) {
		if (this.verticalAlignment != alignment) {
			this.verticalAlignment = alignment;
			invalidateMeasure();
		}
	}

	/**
	 * Gets the static width of the UI element.
	 * @return Width of the UI element.
	 */
	public Float getWidth() {
		return width;
	}
	
	/**
	 * Gets the static height of the UI element.
	 * @return Height of the UI element.
	 */
	public Float getHeight() {
		return height;
	}

	/**
	 * Sets the static size of the UI element.
	 * @param size New size of the UI element.
	 */
	public void setSize(Size size) {
		Float newWidth = size.getWidth();
		Float newHeight = size.getHeight();

		if (width != newWidth || height != newHeight) {
			width = size.getWidth();
			height = size.getHeight();
			invalidateMeasure();
		}
	}

	/**
	 * Sets the static size of the UI element.
	 * @param width New width of the UI element.
	 * @param height New height of the UI element.
	 */
	public void setSize(Float width, Float height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			invalidateMeasure();
		}
	}

	/**
	 * Sets the static width of the UI element.
	 * @param width New width of the UI element.
	 */
	public void setWidth(Float width) {
		if (this.width != width) {
			this.width = width;
			invalidateMeasure();
		}
	}

	/**
	 * Sets the static height of the UI element.
	 * @param height New height of the UI element.
	 */
	public void setHeight(Float height) {
		if (this.height != height) {
			this.height = height;
			invalidateMeasure();
		}
	}

	/**
	 * Gets the minimum allowable width that a UI element can have.
	 * @return Minimum allowable width of the UI element.
	 */
	public Float getMinWidth() {
		return minWidth;
	}

	/**
	 * Sets the minimum allowable width that a UI element can have.
	 * @param minWidth Minimum allowable width to set.
	 */
	public void setMinWidth(Float minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * Gets the minimum allowable height that a UI element can have.
	 * @return Minimum allowable height of the UI element.
	 */
	public Float getMinHeight() {
		return minHeight;
	}

	/**
	 * Sets the minimum allowable height that a UI element can have.
	 * @param minHeight Minimum allowable height to set.
	 */
	public void setMinHeight(Float minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * Gets the maximum allowable width that a UI element can have.
	 * @return Maximum allowable width of the UI element.
	 */
	public Float getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Sets the maximum allowable width that a UI element can have.
	 * @param maxWidth Maximum allowable width to set.
	 */
	public void setMaxWidth(Float maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * Gets the maximum allowable height that a UI element can have.
	 * @return Maximum allowable height of the UI element.
	 */
	public Float getMaxHeight() {
		return maxHeight;
	}

	/**
	 * Sets the maximum allowable height that a UI element can have.
	 * @param maxHeight Maximum allowable height to set.
	 */
	public void setMaxHeight(Float maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * Whether or not the component is currently visible.
	 * @return Visibility state as a boolean.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets whether the component is visible or not
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

	/**
	 * Gets whether a UI element is hit test visible.
	 * @return True if the UI element is hit test visible, false otherwise.
	 */
	public boolean isHitTestVisible() {
		return isHitTestVisible;
	}

	/**
	 * Set whether a UI element should be visible or not.
	 * @param state Whether the UI element should be hit test visible.
	 */
	public void setIsHitTestVisible(boolean state) {
		isHitTestVisible = state;
	}

	/**
	 * Returns the parent of the element.
	 * @return Parent of the component as a ClickGuiTab.
	 */
	public UIElement getParent() {
		return parent;
	}

	/**
	 * Invalidates the measurements (preferredSize) of the UI element.
	 */
	public void invalidateMeasure() {
		if (initialized) {
			measureDirty = true;

			if (parent != null) {
				parent.invalidateMeasure();
			} else {
				// Root element
				Float w = getWidth();
				Float h = getHeight();

				Size size = new Size(
						w != null ? w : 0f,
						h != null ? h : 0f);

				if (minWidth != null && size.getWidth() < minWidth)
					size.setWidth(minWidth);

				if (minHeight != null && size.getHeight() < minHeight)
					size.setHeight(minHeight);

				if (maxWidth != null && size.getWidth() > maxWidth)
					size.setWidth(maxWidth);

				if (maxHeight != null && size.getHeight() > maxHeight)
					size.setHeight(maxHeight);

				measureCore(size);
				float rw = Math.max(size.getWidth(), preferredSize.getWidth());
				float rh = Math.max(size.getHeight(), preferredSize.getHeight());
				Rectangle rect = new Rectangle(0f, 0f, rw, rh);
				arrange(rect);
			}
		}
	}

	/**
	 * Invalidates the layout and actualSize of the UI element.
	 */
	public void invalidateArrange() {
		if (initialized) {
			Float w = getWidth();
			Float h = getHeight();
			float rw = Math.max(w != null ? w : 0f, preferredSize.getWidth());
			float rh = Math.max(h != null ? h : 0f, preferredSize.getHeight());
			Rectangle rect = new Rectangle(0f, 0f, rw, rh);
			arrange(rect);
		}
	}

	/**
	 * Gets the default size of an element given available space.
	 * @param availableSize Space available to the UI element.
	 * @return Default size of the element.
	 */
	protected Size getStartingSize(Size availableSize) {
		return new Size(0f, 0f);
	}

	/**
	 * Measures the UI element accounting for all of the children.
	 * @param availableSize The total amount of space that the UI element has to fit in.
	 */
	public final void measureCore(Size availableSize) {
		if (!isVisible()) {
			return;
		}

		// Do not measure if the UI element is not initialized.
		if (!initialized) {
			return;
		}

		// Skip if not dirty and available size hasn't changed.
		if (!measureDirty &&
			lastMeasureWidth != null && lastMeasureHeight != null &&
			lastMeasureWidth.equals(availableSize.getWidth()) &&
			lastMeasureHeight.equals(availableSize.getHeight())) {
			return;
		}

		preferredSize = measure(availableSize);

		// Apply explicit width/height
		if (width != null) {
			float w = width;
			if (margin != null) {
				if (margin.left() != null) w += margin.left();
				if (margin.right() != null) w += margin.right();
			}
			preferredSize.setWidth(w);
		}

		if (height != null) {
			float h = height;
			if (margin != null) {
				if (margin.top() != null) h += margin.top();
				if (margin.bottom() != null) h += margin.bottom();
			}
			preferredSize.setHeight(h);
		}

		// Apply min/max constraints.
		if (minWidth != null && preferredSize.getWidth() < minWidth)
			preferredSize.setWidth(minWidth);
		else if (maxWidth != null && preferredSize.getWidth() > maxWidth)
			preferredSize.setWidth(maxWidth);

		if (minHeight != null && preferredSize.getHeight() < minHeight)
			preferredSize.setHeight(minHeight);
		else if (maxHeight != null && preferredSize.getHeight() > maxHeight)
			preferredSize.setHeight(maxHeight);

		lastMeasureWidth = availableSize.getWidth();
		lastMeasureHeight = availableSize.getHeight();
		measureDirty = false;
	}

	/**
	 * Measures the UI element accounting for all of the children.
	 * @param availableSize The total amount of space that the UI element has to fit in.
	 * @return The computed preferred size.
	 */
	public Size measure(Size availableSize) {
		Size finalSize = getStartingSize(availableSize);

		// Reduce available space by padding for children.
		Size childAvailableSize = availableSize;
		if (padding != null) {
			float padW = 0f;
			float padH = 0f;
			if (padding.left() != null) padW += padding.left();
			if (padding.right() != null) padW += padding.right();
			if (padding.top() != null) padH += padding.top();
			if (padding.bottom() != null) padH += padding.bottom();
			childAvailableSize = new Size(
					availableSize.getWidth() - padW,
					availableSize.getHeight() - padH);
		}

		for (UIElement element : children) {
			if (!element.visible)
				continue;

			element.measureCore(childAvailableSize);
			Size resultingSize = element.getPreferredSize();

			if (resultingSize.getWidth() > finalSize.getWidth())
				finalSize.setWidth(resultingSize.getWidth());

			if (resultingSize.getHeight() > finalSize.getHeight())
				finalSize.setHeight(resultingSize.getHeight());
		}

		// Add padding to the result.
		if (padding != null) {
			if (padding.left() != null)
				finalSize.setWidth(finalSize.getWidth() + padding.left());
			if (padding.right() != null)
				finalSize.setWidth(finalSize.getWidth() + padding.right());
			if (padding.top() != null)
				finalSize.setHeight(finalSize.getHeight() + padding.top());
			if (padding.bottom() != null)
				finalSize.setHeight(finalSize.getHeight() + padding.bottom());
		}

		if (margin != null) {
			Float marginLeft = margin.left();
			Float marginTop = margin.top();
			Float marginRight = margin.right();
			Float marginBottom = margin.bottom();

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

		return finalSize;
	}

	/**
	 * Arranges the UI element onto the screen.
	 * @param finalSize The final size available to the UI element as deemed by the parent.
	 */
	public void arrange(Rectangle finalSize) {
		if (initialized) {
			Rectangle newFinalSize;
			if (margin != null) {
				newFinalSize = new Rectangle(finalSize);

				Float marginLeft = margin.left();
				Float marginTop = margin.top();
				Float marginRight = margin.right();
				Float marginBottom = margin.bottom();

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


			// Strictly enforce the specified width and heigth.
			if (width != null)
				newFinalSize.setWidth(width);

			if (height != null)
				newFinalSize.setHeight(height);


			if (horizontalAlignment != HorizontalAlignment.Stretch || width != null) {
				// Calculate the total horizontal margin.
				float totalHorizontalMargin= 0f;
				if (margin != null) {
					if (margin.left() != null) totalHorizontalMargin += margin.left();
					if (margin.right() != null) totalHorizontalMargin += margin.right();
				}
				
				// Calculate the desired width.
				float desiredWidth = width != null ? width
						: preferredSize.getWidth() - totalHorizontalMargin;
				desiredWidth = Math.min(desiredWidth, finalSize.getWidth() - totalHorizontalMargin);

				float available = finalSize.getWidth() - totalHorizontalMargin;

				switch (horizontalAlignment) {
				case Left:
				case Stretch:
					newFinalSize.setWidth(desiredWidth);
					break;
				case Center:
					float offsetX = (available - desiredWidth) / 2f;
					newFinalSize.setX(newFinalSize.getX() + offsetX);
					newFinalSize.setWidth(desiredWidth);
					break;
				case Right:
					newFinalSize.setX(newFinalSize.getX() + available - desiredWidth);
					newFinalSize.setWidth(desiredWidth);
					break;
				}
			}

			if (verticalAlignment != VerticalAlignment.Stretch || height != null) {
				// Calculate total amount of vertical margin.
				float totalVerticalMargin = 0f;
				if (margin != null) {
					if (margin.top() != null) totalVerticalMargin += margin.top();
					if (margin.bottom() != null) totalVerticalMargin += margin.bottom();
				}
				
				// Calculate the desired height.
				float desiredHeight = height != null ? height
						: preferredSize.getHeight() - totalVerticalMargin;
				desiredHeight = Math.min(desiredHeight, finalSize.getHeight() - totalVerticalMargin);

				float available = finalSize.getHeight() - totalVerticalMargin;

				switch (verticalAlignment) {
				case Top:
				case Stretch:
					newFinalSize.setHeight(desiredHeight);
					break;
				case Center:
					float offsetY = (available - desiredHeight) / 2f;
					newFinalSize.setY(newFinalSize.getY() + offsetY);
					newFinalSize.setHeight(desiredHeight);
					break;
				case Bottom:
					newFinalSize.setY(newFinalSize.getY() + available - desiredHeight);
					newFinalSize.setHeight(desiredHeight);
					break;
				}
			}

			setActualSize(newFinalSize);

			Rectangle contentArea = getContentArea();
			for (UIElement element : children) {
				element.arrange(contentArea);
			}
		}
	}

	/**
	 * Gets a list of all child UI elements.
	 * @return List of children.
	 */
	public List<UIElement> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Adds a child to the UI element.
	 * @param child Child to add.
	 */
	public void addChild(UIElement child) {
		if (child == null)
			return;

		// Initialize the child if it has not been already.
		if (initialized && !child.initialized)
			child.initialize();

		// Remove the child from the previous parent if one exists.
		if(child.parent != null) {
			child.parent.removeChild(child);
		}
		
		child.parent = this;
		children.add(child);
		onChildAdded(child);
		invalidateMeasure();
	}

	/**
	 * Removes a child from the UI element.
	 * @param child Child to remove.
	 */
	public void removeChild(UIElement child) {
		if (child == null)
			return;

		child.parent = null;
		children.remove(child);
		onChildRemoved(child);
		invalidateMeasure();
	}

	/**
	 * Clears all children from this UI element.
	 */
	public void clearChildren() {
		for (UIElement child : children)
			child.parent = null;
		children.clear();
		invalidateMeasure();
	}

	/**
	 * Fired when a child is added to the UI element.
	 * @param child Child that was added.
	 */
	protected void onChildAdded(UIElement child) { }
	
	/**
	 * Fired when a child is removed from the UI element.
	 * @param child Child that was removed.
	 */
	protected void onChildRemoved(UIElement child) { }

	/**
	 * Fired when a child changes size.
	 * @param child Child that was modified.
	 */
	protected void onChildChanged(UIElement child) {
		invalidateMeasure();
	}

	/**
	 * Fired when the visibility of the UI element changes.
	 */
	protected void onVisibilityChanged() {
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
			float mouseX = (float) event.getX();
			float mouseY = (float) event.getY();

			hovered = actualSize.intersects(mouseX, mouseY);

			// Propagate to children.
			Iterator<UIElement> tabIterator = getChildren().iterator();
			while (tabIterator.hasNext()) {
				tabIterator.next().onMouseMove(event);
			}

			if (!event.isCancelled() && hovered) {
				event.cancel();
				String tooltip = getTooltip();
				GuiManager.setTooltip(tooltip);
			} else if (wasHovered && !hovered) {
				GuiManager.setTooltip(null);
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

		if (!event.isCancelled() && onClicked != null && isHitTestVisible && isVisible()) {
			if (actualSize.intersects((float) event.mouseX, (float) event.mouseY)) {
				onClicked.accept(event);
			}
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

	public Consumer<MouseClickEvent> getOnClicked() {
		return onClicked;
	}

	public void setOnClicked(Consumer<MouseClickEvent> onClicked) {
		this.onClicked = onClicked;
	}
}
