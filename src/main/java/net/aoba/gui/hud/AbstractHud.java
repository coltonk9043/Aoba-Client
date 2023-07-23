package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.gui.IMoveable;
import net.aoba.misc.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class AbstractHud implements IMoveable {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	protected RenderUtils renderUtils = new RenderUtils();
	protected MinecraftClient mc = MinecraftClient.getInstance();
	
	public AbstractHud(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x) && mouseX <= (x + width)) {
					if (mouseY >= (y) && mouseY <= (y + height)) {
						if (mouseClicked) {
							HudManager.currentGrabbed = this;
						}
					}
				}
			}
		}
	}
	
	public abstract void draw(DrawContext drawContext, float partialTicks, Color color);
}
