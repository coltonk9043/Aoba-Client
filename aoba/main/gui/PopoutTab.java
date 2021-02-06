package aoba.main.gui;

import aoba.main.gui.elements.Component;

public class PopoutTab extends Tab{

	private boolean isVisible;
	
	public PopoutTab(String title, int x, int y) {
		super(title, x, y);
		this.isVisible = false;
	}

	public void setVisible(boolean bool) {
		this.isVisible = bool;
	}
	
	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if (mc.aoba.hm.isClickGuiOpen()) {
			if(this.isVisible) {
				for (Component child : this.children) {
					child.update(mouseX, mouseY, mouseClicked);
				}
			}
		}
	}
	
	@Override
	public void draw() {
		// Draws background depending on components width and height
		if(this.isVisible) {
			renderUtils.drawOutlinedBox(x, y + 14, width, height, 0.3f, 0.3f, 0.3f, 0.65f);
			for (Component child : children) {
				child.draw();
			}
		}
	}
}
