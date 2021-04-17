package aoba.main.gui;

import java.util.ArrayList;
import aoba.main.gui.elements.Component;
import net.minecraft.client.Minecraft;

public class ClickGuiTab extends Tab {

	protected Minecraft mc;
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
		this.mc = Minecraft.getInstance();
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
		
		if (mc.aoba.hm.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x *  mc.gameSettings.guiScale) && mouseX <= (x + width) *  mc.gameSettings.guiScale) {
					if (mouseY >= (y *  mc.gameSettings.guiScale) && mouseY <= (y + 14) *  mc.gameSettings.guiScale) {
						if (mouseClicked) {
							boolean isInsidePinButton = false;
							if (mouseX >= (x + width - 12) *  mc.gameSettings.guiScale && mouseX <= (x + width - 2) *  mc.gameSettings.guiScale) {
								if (mouseY >= (y + 2) *  mc.gameSettings.guiScale && mouseY <= (y + 10) *  mc.gameSettings.guiScale) {
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
			int i = 15;
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
	public void draw(int scaledWidth, int scaledHeight, Color color) {
		if(drawBorder) {
			// Draws background depending on components width and height
			renderUtils.drawOutlinedBox(x, y, width, 14, 0.3f, 0.3f, 0.3f, 0.4f);
			mc.fontRenderer.drawStringWithShadow(this.title, x + 4, y + 4, mc.aoba.hm.getColor().getColorAsInt(), true);
			renderUtils.drawOutlinedBox(x, y + 14, width, height, 0.3f, 0.3f, 0.3f, 0.4f);
			if (this.isPinned) {
				renderUtils.drawOutlinedBox(x + width - 12, y + 2, 10, 10, 0.6f, 0.0f, 0.0f, 0.8f);
			} else {
				renderUtils.drawOutlinedBox(x + width - 12, y + 2, 10, 10, 0.5f, 0.5f, 0.5f, 0.2f);
			}
		}
		int i = 15;
		for (Component child : children) {
			child.draw(i, scaledWidth, scaledHeight, color);
			i += child.getHeight();
		}
	}
}
