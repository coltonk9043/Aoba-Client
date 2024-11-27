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

// An interface containing the most abstract definition of a Hud Element that will appear on the screen. 
package net.aoba.gui;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.colors.Colors;
import net.aoba.gui.navigation.huds.ModuleSelectorHud;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class UIElement {
	public static boolean DEBUG = false;

	protected static MinecraftClient MC = MinecraftClient.getInstance();
	protected static AobaClient AOBA = Aoba.getInstance();

	protected ArrayList<UIElement> children = new ArrayList<UIElement>();
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

	protected boolean visible = true;
	protected boolean hovered = false;
	protected boolean isHitTestVisible = true;

	public UIElement(UIElement parent) {
		this.parent = parent;
		preferredSize = new Size(0.0f, 0.0f);
		actualSize = new Rectangle(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void initialize() {
		this.initialized = true;

		for (UIElement child : children) {
			if (child == null)
				continue;
			child.initialize();
		}

		invalidate();
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
			Render2D.drawBoxOutline(drawContext.getMatrices().peek().getPositionMatrix(), actualSize, Colors.Red);
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
			invalidate();
		}
	}

	public Float getWidth() {
		return width;
	}

	public void setSize(Size size) {
		Float newWidth = size.getWidth();
		Float newHeight = size.getHeight();

		if (this.width != newWidth || this.height != newHeight) {
			this.width = size.getWidth();
			this.height = size.getHeight();
			invalidate();
		}
	}

	public void setSize(Float width, Float height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			invalidate();
		}
	}

	public void setWidth(Float width) {
		if (this.width != width) {
			this.width = width;
			invalidate();
		}
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		if (this.height != height) {
			if (this instanceof ModuleSelectorHud) {
				System.out.println();
			}

			this.height = height;
			invalidate();
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

		// for (UIElement child : children) {
		// child.setVisible(bool);
		// }

		// If parent is not null, notify the parent.
		if (parent != null) {
			parent.onChildChanged(this);
		}

		onVisibilityChanged();
	}

	public boolean isHitTestVisible() {
		return this.isHitTestVisible;
	}

	public void setIsHitTestVisible(boolean state) {
		this.isHitTestVisible = state;
	}

	/**
	 * Returns the parent of the Component.
	 *
	 * @return Parent of the component as a ClickGuiTab.
	 */
	public UIElement getParent() {
		return parent;
	}

	public void invalidate() {
		if (initialized) {
			if (parent != null)
				parent.invalidate();
			else {
				// Construct new bounds based off of width/height constraints.
				Size size = new Size(0f, 0f);

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

	/**
	 * Measures the UI element accounting for all of the children. This method spans
	 * all of the children, starting from the bottom up.
	 * 
	 * @param availableSize The total amount of space that the UI element has to fit
	 *                      in.
	 * @return The new preferred size of the UI Element.
	 */
	public void measure(Size availableSize) {
		if (initialized) {
			float finalWidth = availableSize.getWidth();
			float finalHeight = availableSize.getHeight();

			for (UIElement element : children) {
				if (!element.visible)
					continue;

				element.measure(availableSize);
				Size resultingSize = element.getPreferredSize();

				if (resultingSize.getWidth() > finalWidth)
					finalWidth = resultingSize.getWidth();

				if (resultingSize.getHeight() > finalHeight)
					finalHeight = resultingSize.getHeight();
			}

			if (margin != null) {

				Float marginLeft = margin.getLeft();
				Float marginTop = margin.getTop();
				Float marginRight = margin.getRight();
				Float marginBottom = margin.getBottom();

				if (marginLeft != null)
					finalWidth += marginLeft;

				if (marginRight != null)
					finalWidth += marginRight;

				if (marginTop != null)
					finalHeight += marginTop;

				if (marginBottom != null)
					finalHeight += marginBottom;
			}

			if (minWidth != null && finalWidth < minWidth) {
				finalWidth = minWidth;
			} else if (maxWidth != null && finalWidth > maxWidth) {
				finalWidth = maxWidth;
			}

			if (minHeight != null && finalHeight < minHeight) {
				finalHeight = minHeight;
			} else if (maxHeight != null && finalHeight > maxHeight) {
				finalHeight = maxHeight;
			}

			preferredSize = new Size(finalWidth, finalHeight);
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

			setActualSize(newFinalSize);

			for (UIElement element : children) {
				element.arrange(this.getActualSize());
			}
		}
	}

	public void addChild(UIElement child) {
		if (child == null)
			return;

		children.add(child);
		onChildAdded(child);
	}

	public void removeChild(UIElement child) {
		if (child == null)
			return;

		children.remove(child);
		onChildRemoved(child);
	}

	public void onChildAdded(UIElement child) {
		invalidate();
	}

	public void onChildChanged(UIElement child) {
		invalidate();
	}

	public void onChildRemoved(UIElement child) {
		invalidate();
	}

	public void onVisibilityChanged() {
		invalidate();
	}

	public void dispose() {

	}

	public void onMouseMove(MouseMoveEvent event) {
	}

	public void onMouseClick(MouseClickEvent event) {

	}
}
