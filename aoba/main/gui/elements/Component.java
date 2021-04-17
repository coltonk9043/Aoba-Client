package aoba.main.gui.elements;

import aoba.main.gui.ClickGuiTab;
import aoba.main.gui.Color;
import aoba.main.misc.RenderUtils;

public abstract class Component {
	protected RenderUtils renderUtils = new RenderUtils();
	
	private int height = 14;
	protected ClickGuiTab parent;
	
	public Component() {

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
	public abstract void draw(int offset, int scaledWidth, int scaledHeight, Color color);
}
