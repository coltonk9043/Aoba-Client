package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.Color;
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
	
	public Component() {
		this.renderUtils = Aoba.getInstance().renderUtils;
		Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
	}
	
	public void setVisible(boolean bool) {
		this.visible = bool;
		if(bool) {
			Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
		}else {
			Aoba.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
		}
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public ClickGuiTab getParent()
	{
		return parent;
	}
	
	public void setParent(ClickGuiTab parent)
	{
		this.parent = parent;
	}

	public void update(int offset) {
		this.offset = offset;
	}
	
	public abstract void draw(int offset, DrawContext drawContext, float partialTicks, Color color);
	
	@Override
	public void OnMouseMove(MouseMoveEvent mouseMoveEvent) {
		System.out.println("Mouse Move Component");
		float parentX = parent.getX();
		float parentY = parent.getY();
		float parentWidth = parent.getWidth();
		
		double mouseX = mouseMoveEvent.GetHorizontal();
		double mouseY = mouseMoveEvent.GetVertical();
		
		this.hovered = ((mouseX >= parentX && mouseX <= (parentX + parentWidth)) && (mouseY >= parentY + offset && mouseY <= (parentY + offset + 28)));
	}
}
