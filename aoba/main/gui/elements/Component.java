package aoba.main.gui.elements;

import aoba.main.gui.Tab;
import aoba.main.misc.RenderUtils;

public abstract class Component {
	protected RenderUtils renderUtils = new RenderUtils();
	
	private int height = 14;
	private Tab parent;
	private int id;
	
	public Component(int id) {
		this.id = id;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public Tab getParent()
	{
		return parent;
	}
	
	public void setParent(Tab parent)
	{
		this.parent = parent;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}

	public abstract void update(double mouseX, double mouseY, boolean mouseClicked);
	public abstract void draw();
}
