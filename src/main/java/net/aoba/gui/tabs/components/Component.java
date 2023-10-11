package net.aoba.gui.tabs.components;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.IHudElement;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public abstract class Component implements IHudElement, MouseMoveListener {
	protected static RenderUtils renderUtils;
	
	private static boolean DEBUG = true;
	
	private boolean visible = true;
	protected boolean hovered = false;
	
	// NEW ui variables.
	protected IHudElement parent;
	protected ArrayList<Component> children;
	
	// These are positions that the UI designer will input to enforce specific options. 
	// These will take precedence over the top/bottom/left/right positions. 
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	
	protected boolean autoWidth;
	protected boolean autoHeight;
	
	private float top;
	private float bottom;
	private float left;
	private float right;
	
	// The actual screen space positions of the HUD elements. 
	protected float actualX;
	protected float actualY;
	protected float actualHeight;
	protected float actualWidth;
	
	public Component(IHudElement parent) {
		this.parent = parent;
		this.children = new ArrayList<Component>();
		
		// Assumes that the component will take up the entire space of the parent unless otherwise specified.
		this.setTop(0);
		this.setLeft(0);
		this.setRight(0);
		this.setBottom(0);
		
		if(renderUtils == null) {
			renderUtils = Aoba.getInstance().renderUtils;
		}
		Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
	}
	
	/**
	 * Gets the X position of the component.
	 * @return X position of the component as a float.
	 */
	@Override
	public float getX() {
		return actualX;
	}
	
	/**
	 * Gets the Y position of the component.
	 * @return Y position of the component as a float.
	 */
	@Override
	public float getY() {
		return actualY;
	}
	
	/**
	 * Gets the Width of the component.
	 * @return Width of the component as a float.
	 */
	@Override
	public float getWidth() {
		return actualWidth;
	}
	
	/**
	 * Gets the height of the component.
	 * @return Height of the component as a float.
	 */
	@Override
	public float getHeight()
	{
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
		if(this.x != x) {
			this.x = x;
			this.actualX = this.parent.getX() + x;
			updateChildrenPosition();
		}
	}
	
	public void setY(float y) {
		if(this.y != y) {
			this.y = y;
			this.actualY = this.parent.getY() + y;
			updateChildrenPosition();
		}
	}
	
	public void setWidth(float width) {
		if(this.width != width) {
			this.width = width;
			this.actualWidth = width;
			updateChildrenPosition();
		}
	}
	
	/**
	 * Sets the height of the component.
	 * @param height The height to set.
	 */
	@Override
	public void setHeight(float height)
	{
		if(this.height != height) {
			this.height = height;
			this.actualHeight = height;
			updateChildrenPosition();
		}
	}
	
	public void setTop(float top) {
		if(this.top != top) {
			this.top = top;
			if(y == 0.0f) {
				actualY = parent.getY() + top;	
			}else {
				actualY = y;
			}
			if(height == 0.0f) {
				actualHeight = parent.getHeight() - top - bottom;
			}else {
				actualHeight = height;
			}	
			updateChildrenPosition();
		}
	}
	
	public void setBottom(float bottom) {
		if(this.bottom != bottom) {
			this.bottom = bottom;
			if(height == 0.0) {
				actualHeight = parent.getHeight() - top - bottom;
			}else {
				actualHeight = height;
			}
			updateChildrenPosition();
		}
	}
	
	public void setLeft(float left) {
		if(this.left != left) {
			this.left = left;
			if(x == 0.0) {
				actualX = parent.getX() + left;
			}else {
				actualX = x;
			}
			
			if(width == 0.0f) {
				actualWidth = parent.getWidth() - left - right;
			}else {
				actualWidth = width;
			}	
			updateChildrenPosition();
		}
	}
	
	public void setRight(float right) {
		if(this.right != right) {
			this.right = right;
			if(width == 0.0f) {
				actualWidth = parent.getWidth() - right - left;
			}else {
				actualWidth = width;
			}
			updateChildrenPosition();
		}
	}
	
	public void addChild(Component component) {
		this.children.add(component);
	}
	
	protected void updateChildrenPosition() {
		for(Component child : children) {
			child.onParentMoved();
		}
	}
	
	/**
	 * Updates the position of these elements whenever the parent is moved.
	 */
	public void onParentMoved() {
		// Set Y and Height
		if(y == 0.0f) {
			actualY = parent.getY() + top;	
		}else {
			actualY = y;
		}
		if(height == 0.0f) {
			actualHeight = parent.getHeight() - top - bottom;
		}else {
			actualHeight = height;
		}	
		
		// Set X and Width
		if(x == 0.0) {
			actualX = parent.getX() + left;
		}else {
			actualX = x;
		}
		
		if(width == 0.0f) {
			actualWidth = parent.getWidth() - left - right;
		}else {
			actualWidth = width;
		}	
		updateChildrenPosition();
	}
	
	/**
	 * Returns the parent of the Component.
	 * @return Parent of the component as a ClickGuiTab.
	 */
	public IHudElement getParent()
	{
		return parent;
	}
	
	/**
	 * Sets whether the component is visible or not
	 * @param bool State to set visibility to.
	 */
	public void setVisible(boolean bool) {
		this.visible = bool;
		
		// Register and Unregister event listener according to state.
		if(bool) {
			Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
		}else {
			Aoba.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
		}
	}
	
	/**
	 * Whether or not the component is currently visible.
	 * @return Visibility state as a boolean.
	 */
	public boolean isVisible() {
		return this.visible;
	}
	
	/**
	 * Updates the offset (y position relative to parent) of the component.
	 * @param offset Offset 
	 */
	public void update() {
		for(Component child : children) {
			if(child.visible) {
				child.update();
			}
		}
	}
	
	/**
	 * Abstract method for drawing components onto the screen.
	 * @param offset Offset of the module.
	 * @param drawContext DrawContext of the game.
	 * @param partialTicks Partial Ticks of the game.
	 * @param color Color of the UI.
	 */
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		if(this.hovered && DEBUG) {
			renderUtils.drawOutline(drawContext.getMatrices(), this.actualX, this.actualY, this.actualWidth, this.actualHeight);
		}

		for(Component child : children) {
			if(child.visible) {
				child.draw(drawContext, partialTicks, color);
			}
		}
	}
	
	/**
	 * Triggers when the mouse is moved.
	 * @param mouseMoveEvent Event fired.
	 */
	@Override
	public void OnMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (HudManager.currentGrabbed != null || !visible) {
			this.hovered = false;
		}else {
				double mouseX = mouseMoveEvent.GetHorizontal();
				double mouseY = mouseMoveEvent.GetVertical();
				
				this.hovered = ((mouseX >= actualX && mouseX <= (actualX + actualWidth)) && (mouseY >= (actualY) && mouseY <= (actualY + actualHeight)));	
		}
	}
}
