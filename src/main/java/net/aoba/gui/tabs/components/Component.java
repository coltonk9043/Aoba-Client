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

import java.util.ArrayList;
import net.aoba.Aoba;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.IGuiElement;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public abstract class Component implements IGuiElement, MouseMoveListener {
	private static boolean DEBUG = false;

	private boolean visible = false;
	protected boolean hovered = false;

	// NEW ui variables.
	protected IGuiElement parent;
	protected ArrayList<Component> children;

	// These are positions that the UI designer will input to enforce specific
	// options.
	// These will take precedence over the top/bottom/left/right positions.
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	protected boolean autoWidth;
	protected boolean autoHeight;

	private float top = -1;
	private float bottom = -1;
	private float left = -1;
	private float right = -1;

	// The actual screen space positions of the HUD elements.
	protected float actualX;
	protected float actualY;
	protected float actualHeight;
	protected float actualWidth;

	public Component(IGuiElement parent) {
		this.parent = parent;
		this.children = new ArrayList<Component>();

		// Assumes that the component will take up the entire space of the parent unless
		// otherwise specified.
		this.setTop(0);
		this.setLeft(0);
		this.setRight(0);
		this.setBottom(0);
	}

	/**
	 * Gets the X position of the component.
	 * 
	 * @return X position of the component as a float.
	 */
	@Override
	public float getX() {
		return actualX;
	}

	/**
	 * Gets the Y position of the component.
	 * 
	 * @return Y position of the component as a float.
	 */
	@Override
	public float getY() {
		return actualY;
	}

	/**
	 * Gets the Width of the component.
	 * 
	 * @return Width of the component as a float.
	 */
	@Override
	public float getWidth() {
		return actualWidth;
	}

	/**
	 * Gets the height of the component.
	 * 
	 * @return Height of the component as a float.
	 */
	@Override
	public float getHeight() {
		return actualHeight;
	}

	public float getTop() {
		return top;
	}

	public float getBottom() {
		return bottom;
	}

	public float getLeft() {
		return left;
	}

	public float getRight() {
		return right;
	}

	public void setX(float x) {
		if (this.x != x) {
			this.x = x;
			this.setActualX(x);
			
			if(this.parent != null) {
				this.parent.OnChildChanged(this);
			}
		}
	}

	public void setY(float y) {
		if (this.y != y) {
			this.y = y;
			this.setActualY(y);

			// If parent is not null, notify the parent.
			if (parent != null) {
				this.parent.OnChildChanged(this);
			}
		}
	}

	public void setWidth(float width) {
		if (this.width != width) {
			this.width = width;
			this.setActualWidth(width);

			// If parent is not null, notify the parent.
			if (parent != null) {
				this.parent.OnChildChanged(this);
			}
		}
	}

	/**
	 * Sets the height of the component.
	 * 
	 * @param height The height to set.
	 */
	@Override
	public void setHeight(float height) {
		if (this.height != height) {
			this.height = height;
			this.setActualHeight(height);

			// If parent is not null, notify the parent.
			if (parent != null) {
				this.parent.OnChildChanged(this);
			}
		}
	}

	public void setTop(float top) {
		if (this.top != top) {
			this.top = top;

			// If parent is not null, notify the parent and use it's positioning.
			if (parent != null) {
				if (y == 0.0f) {
					this.setActualY(parent.getY() + top);
				}

				if (height == 0.0f) {
					this.setActualHeight(parent.getHeight() - top - bottom);
				}
				
				this.parent.OnChildChanged(this);
			}else {
				if (y == 0.0f) {
					this.setActualY(top);
				}

				if (height == 0.0f) {
					this.setActualHeight(top - bottom);
				}
			}
		}
	}

	public void setBottom(float bottom) {
		if (this.bottom != bottom) {
			this.bottom = bottom;
			

			// If parent is not null, notify the parent.
			if (parent != null) {
				if (height == 0.0) {
					this.setActualHeight(parent.getHeight() - top - bottom);
				}
				this.parent.OnChildChanged(this);
			}else {
				if (height == 0.0) {
					this.setActualHeight(top - bottom);
				}
			}
		}
	}

	public void setLeft(float left) {
		if (this.left != left) {
			this.left = left;
			
			// If parent is not null, notify the parent.
			if (parent != null) {
				if (x == 0.0) {
					this.setActualX(parent.getX() + left);
				}
				if (width == 0.0f) {
					this.setActualWidth(parent.getWidth() - left - right);
				}
				this.parent.OnChildChanged(this);
			}else {
				if (x == 0.0) {
					this.setActualX(left);
				}
				if (width == 0.0f) {
					this.setActualWidth(left - right);
				}
			}
		}
	}

	public void setRight(float right) {
		if (this.right != right) {
			this.right = right;
			
			// If parent is not null, notify the parent.
			if (parent != null) {
				if (width == 0.0f) {
					this.setActualWidth(parent.getWidth() - right - left);
				}
				
				this.parent.OnChildChanged(this);
			}else {
				if (width == 0.0f) {
					this.setActualWidth(right - left);
				}
			}
		}
	}

	private void setActualX(float value) {
		if (actualX == value)
			return;

		actualX = value;
		for (Component child : children) {
			child.OnParentXChanged();
		}
	}

	private void setActualY(float value) {
		if (actualY == value)
			return;

		actualY = value;
		for (Component child : children) {
			child.OnParentYChanged();
		}
	}

	private void setActualWidth(float value) {
		if (actualWidth == value)
			return;

		actualWidth = value;
		for (Component child : children) {
			child.OnParentWidthChanged();
		}
	}

	private void setActualHeight(float value) {
		if (actualHeight == value)
			return;

		actualHeight = value;
		for (Component child : children) {
			child.OnParentHeightChanged();
		}
	}

	public void addChild(Component component) {
		this.children.add(component);
		this.OnChildAdded(component);
	}

	public void OnParentXChanged() {
		if (x == 0.0) {
			setActualX(parent.getX() + left);
		}
	}

	public void OnParentYChanged() {
		if (y == 0.0f) {
			setActualY(parent.getY() + top);
		}
	}

	public void OnParentWidthChanged() {
		if (width == 0.0f) {
			setActualWidth(parent.getWidth() - left - right);
		}
	}

	public void OnParentHeightChanged() {
		if (height == 0.0f) {
			setActualHeight(parent.getHeight() - top - bottom);
		}
	}

	/**
	 * Returns the parent of the Component.
	 * 
	 * @return Parent of the component as a ClickGuiTab.
	 */
	public IGuiElement getParent() {
		return parent;
	}

	/**
	 * Sets whether the component is visible or not
	 * 
	 * @param bool State to set visibility to.
	 */
	public void setVisible(boolean bool) {
		if (this.visible == bool)
			return;

		this.visible = bool;
		this.hovered = false;

		for (Component child : this.children) {
			child.setVisible(bool);
		}

		// If parent is not null, notify the parent.
		if (parent != null) {
			this.parent.OnChildChanged(this);
		}


		// Register and Unregister event listener according to state.
		if (bool) {
			System.out.println("Registering");
			Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
		} else {
			System.out.println("Unregistering");
			Aoba.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
		}

		this.OnVisibilityChanged();
	}

	/**
	 * Whether or not the component is currently visible.
	 * 
	 * @return Visibility state as a boolean.
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * Updates the offset (y position relative to parent) of the component.
	 * 
	 * @param offset Offset
	 */
	public void update() {
		for (Component child : children) {
			if (child.visible) {
				child.update();
			}
		}
	}

	/**
	 * Abstract method for drawing components onto the screen.
	 * 
	 * @param offset       Offset of the module.
	 * @param drawContext  DrawContext of the game.
	 * @param partialTicks Partial Ticks of the game.
	 * @param color        Color of the UI.
	 */
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		if (this.visible) {
			if (this.hovered && DEBUG) {
				RenderUtils.drawOutline(drawContext.getMatrices(), this.actualX, this.actualY, this.actualWidth,
						this.actualHeight);
			}

			for (Component child : children) {
				if (child.visible) {
					child.draw(drawContext, partialTicks, color);
				}
			}
		}
	}

	/**
	 * Triggers when the mouse is moved.
	 * 
	 * @param mouseMoveEvent Event fired.
	 */
	@Override
	public void OnMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (!visible || (parent != null && !Aoba.getInstance().hudManager.isClickGuiOpen())) {
			this.hovered = false;
		} else {

			double mouseX = mouseMoveEvent.GetHorizontal();
			double mouseY = mouseMoveEvent.GetVertical();

			this.hovered = ((mouseX >= actualX && mouseX <= (actualX + actualWidth))
					&& (mouseY >= (actualY) && mouseY <= (actualY + actualHeight)));
		}
	}

	public void OnChildAdded(IGuiElement child) {

	}

	@Override
	public void OnChildChanged(IGuiElement child) {

	}

	public void OnVisibilityChanged() {

	}
}
