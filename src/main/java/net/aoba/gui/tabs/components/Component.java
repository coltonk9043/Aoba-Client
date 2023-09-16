package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.tabs.ClickGuiTab;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public abstract class Component implements MouseMoveListener {
	protected RenderUtils renderUtils;
	protected ClickGuiTab parent;
	private int height = 30;
	private boolean visible = true;
	protected int offset;
	protected boolean hovered = false;
	
	public Component(ClickGuiTab parent) {
		this.parent = parent;
		this.renderUtils = Aoba.getInstance().renderUtils;
		Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
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
	 * Gets the height of the component.
	 * @return Height of the component as an integer.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Sets the height of the component.
	 * @param height The height to set.
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/**
	 * Returns the parent of the Component.
	 * @return Parent of the component as a ClickGuiTab.
	 */
	public ClickGuiTab getParent()
	{
		return parent;
	}

	/**
	 * Updates the offset (y position relative to parent) of the component.
	 * @param offset Offset 
	 */
	public void update(int offset) {
		this.offset = offset;
	}
	
	/**
	 * Abstract method for drawing components onto the screen.
	 * @param offset Offset of the module.
	 * @param drawContext DrawContext of the game.
	 * @param partialTicks Partial Ticks of the game.
	 * @param color Color of the UI.
	 */
	public abstract void draw(int offset, DrawContext drawContext, float partialTicks, Color color);
	
	/**
	 * Triggers when the mouse is moved.
	 * @param mouseMoveEvent Event fired.
	 */
	@Override
	public void OnMouseMove(MouseMoveEvent mouseMoveEvent) {
		if (HudManager.currentGrabbed != null) {
			this.hovered = false;
		}else {
			if(this.parent != null) {
				float parentX = parent.getX();
				float parentY = parent.getY();
				float parentWidth = parent.getWidth();
				
				double mouseX = mouseMoveEvent.GetHorizontal();
				double mouseY = mouseMoveEvent.GetVertical();
				
				this.hovered = ((mouseX >= parentX && mouseX <= (parentX + parentWidth)) && (mouseY >= (parentY + offset) && mouseY <= (parentY + offset + 28)));	
			}
		}
	}
}
