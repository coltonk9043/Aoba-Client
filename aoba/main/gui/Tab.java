package aoba.main.gui;

import java.util.ArrayList;

import aoba.main.gui.elements.Component;
import aoba.main.misc.RenderUtils;
import net.minecraft.client.Minecraft;

public class Tab {
	protected Minecraft mc;

	protected String title;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected RenderUtils renderUtils = new RenderUtils();

	protected boolean isPinned = false;
	protected boolean pinWasClicked = false;

	protected ArrayList<Component> children = new ArrayList<>();

	public Tab(String title, int x, int y) {
		this.title = title;
		this.x = x;
		this.y = y;
		this.width = 85;
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
		int tempHeight = 1;
		for (Component child : children) {
			tempHeight += (child.getHeight() + 1);
		}
		this.height = tempHeight;
	}

	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if (mc.aoba.hm.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x * 2) && mouseX <= (x + width) * 2) {
					if (mouseY >= (y * 2) && mouseY <= (y + 14) * 2) {
						if (mouseClicked) {
							boolean isInsidePinButton = false;
							if (mouseX >= (x + width - 12) * 2 && mouseX <= (x + width - 2) * 2) {
								if (mouseY >= (y + 2) * 2 && mouseY <= (y + 10) * 2) {
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
			for (Component child : this.children) {
				child.update(mouseX, mouseY, mouseClicked);
			}
		}
	}

	public void preupdate() {
	}

	public void postupdate() {
	}

	public void draw() {
		// Draws background depending on components width and height
		renderUtils.drawOutlinedBox(x, y, width, 14, 0.3f, 0.3f, 0.3f, 0.65f);
		mc.fontRenderer.drawStringWithShadow(this.title, x + 4, y + 4, mc.aoba.hm.getColor(), true);
		renderUtils.drawOutlinedBox(x, y + 14, width, height, 0.3f, 0.3f, 0.3f, 0.65f);
		if (mc.aoba.hm.isClickGuiOpen())
			renderUtils.drawOutlinedBox(x + width - 12, y + 2, 10, 10, 0.6f, 0.0f, 0.0f, isPinned ? 0.8f : 0f);
		for (Component child : children) {
			child.draw();
		}
	}

	public void moveWindow(int x, int y) {
		this.x = (int) (this.x - x);
		this.y = (int) (this.y - y);
	}
}
