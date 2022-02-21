package net.aoba.gui.elements;

import net.aoba.Aoba;
import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.Color;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Component {
	protected RenderUtils renderUtils;
	private int height = 30;
	protected ClickGuiTab parent;
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
	public abstract void draw(int offset, MatrixStack matrixStack, float partialTicks, Color color);
}
