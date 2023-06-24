package net.aoba.gui.tabs;

import net.aoba.gui.Color;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class Tab {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected RenderUtils renderUtils = new RenderUtils();
	protected MinecraftClient mc = MinecraftClient.getInstance();
	
	public abstract void update(double mouseX, double mouseY, boolean mouseClicked) ;
	
	public abstract void draw(DrawContext drawContext, float partialTicks, Color color);
	
	public void moveWindow(int x, int y) {
		this.x = (int) (this.x - x);
		this.y = (int) (this.y - y);
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

}
