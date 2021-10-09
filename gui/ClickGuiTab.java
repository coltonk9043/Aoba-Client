package net.aoba.gui;

import java.util.ArrayList;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.elements.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;


public class ClickGuiTab extends Tab {

	protected MinecraftClient mc;
	protected String title;
	protected boolean isPinned = false;
	protected boolean pinWasClicked = false;
	protected boolean drawBorder = true;
	protected ArrayList<Component> children = new ArrayList<>();

	public ClickGuiTab(String title, int x, int y) {
		this.title = title;
		this.x = x;
		this.y = y;
		this.width = 90;
		this.mc = MinecraftClient.getInstance();
	}

	public final String getTitle() {
		return title;
	}

	public final boolean isPinned() {
		return this.isPinned;
	}

	public final void setPinned(boolean pin) {
		this.isPinned = pin;
	}

	public final boolean getPinClicked() {
		return this.pinWasClicked;
	}

	public final void setPinClicked(boolean pin) {
		this.pinWasClicked = pin;
	}

	public final void setTitle(String title) {

		this.title = title;
	}

	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}

	public final int getWidth() {
		return width;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final int getHeight() {
		return height;
	}

	public final void setHeight(int height) {
		this.height = height;
	}

	public final boolean getPinned() {
		return this.isPinned;
	}

	public final boolean isGrabbed() {
		return (HudManager.currentGrabbed == this);
	}

	public final void addChild(Component component) {
		this.children.add(component);
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		int tempHeight = 1;
		for (Component child : children) {
			tempHeight += (child.getHeight());
		}
		this.height = tempHeight;
		
		if (Aoba.getInstance().hm.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x) && mouseX <= (x + width)) {
					if (mouseY >= (y) && mouseY <= (y + 28)) {
						if (mouseClicked) {
							boolean isInsidePinButton = false;
							if (mouseX >= (x + width - 24) && mouseX <= (x + width - 2)) {
								if (mouseY >= (y + 4) && mouseY <= (y + 20)) {
									isInsidePinButton = true;
								}
							}
							if (isInsidePinButton) {
								if (!this.pinWasClicked) {
									this.isPinned = !this.isPinned;
									this.pinWasClicked = true;
									return;
								}
							} else {
								HudManager.currentGrabbed = this;
							}
						} else {
							if (this.pinWasClicked) {
								this.pinWasClicked = false;
							}
						}
					}
				}
			}
			int i = 30;
			for (Component child : this.children) {
				child.update(i, mouseX, mouseY, mouseClicked);
				i += child.getHeight();
			}
		}
	}

	public void preupdate() {
	}

	public void postupdate() {
	}

	@Override
	public void draw(MatrixStack matrixStack, float partialTicks, Color color) {
		if(drawBorder) {
			// Draws background depending on components width and height
			renderUtils.drawOutlinedBox(matrixStack, x, y, width, 14, new Color(0.3f,0.3f,0.3f), 0.4f);
			mc.textRenderer.drawWithShadow(matrixStack, this.title, x + 4, y + 4, Aoba.getInstance().hm.getColor().getColorAsInt());
			renderUtils.drawOutlinedBox(matrixStack, x, y + 14, width, height, new Color(0.3f,0.3f,0.3f), 0.4f);
			if (this.isPinned) {
				renderUtils.drawOutlinedBox(matrixStack, x + width - 12, y + 2, 10, 10, new Color(0.6f,0.0f,0.0f), 0.8f);
			} else {
				renderUtils.drawOutlinedBox(matrixStack, x + width - 12, y + 2, 10, 10, new Color(0.5f,0.5f,0.5f), 0.2f);
			}
		}
		int i = 15;
		for (Component child : children) {
			child.draw(i, matrixStack, partialTicks, color);
			i += child.getHeight();
		}
	}
}
