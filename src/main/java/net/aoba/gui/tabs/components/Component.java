package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.gui.Color;
import net.aoba.gui.tabs.ClickGuiTab;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Component {
	protected RenderUtils renderUtils;
	protected ClickGuiTab parent;
	private int height = 30;
	private boolean visible = true;
	
	public Component() {
		this.renderUtils = Aoba.getInstance().renderUtils;
	}
	
	public void setVisible(boolean bool) {
		this.visible = bool;
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

	public abstract void update(int offset, double mouseX, double mouseY, boolean mouseClicked);
	public abstract void draw(int offset, DrawContext drawContext, float partialTicks, Color color);
}
