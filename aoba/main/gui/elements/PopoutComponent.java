package aoba.main.gui.elements;

import aoba.main.gui.HudManager;
import aoba.main.gui.PopoutTab;
import aoba.main.gui.Tab;
import aoba.main.module.Module;
import net.minecraft.client.Minecraft;

public class PopoutComponent extends Component {

	private String text;
	private Tab parent;
	private boolean wasClicked = false;
	private boolean popped = false;

	private PopoutTab tabOpened;
	
	public PopoutComponent(int id, String text, Tab parent) {
		super(id);
		this.text = text;
		this.parent = parent;
		tabOpened = new PopoutTab(text, parent.getX() + parent.getWidth() - 5, parent.getY() + (this.getId() * 15));
	}
	
	public void addComponent(Component component) {
		this.tabOpened.addChild(component);
	}
	
	public Tab getTabOpened() {
		return this.tabOpened;
	}

	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		if (HudManager.currentGrabbed == null) {
			if (mouseX >= ((parentX + 1) * 2) && mouseX <= (((parentX + 1)) + parentWidth - 2) * 2) {
				if (mouseY >= (((parentY + 15 + (this.getId() * 15))) * 2)
						&& mouseY <= ((parentY + 15 + (this.getId() * 15)) + 14) * 2) {
					if (mouseClicked) {
						if (!this.wasClicked) {
							this.popped = !this.popped;
							this.wasClicked = true;
						}
					} else {
						if (this.wasClicked) {
							this.wasClicked = false;
						}
					}
				}
			}
		}
		this.tabOpened.setVisible(popped);
		this.tabOpened.setX(parentX + parentWidth);
		this.tabOpened.setY(parentY + (this.getId() * 15));
		this.tabOpened.update(mouseX, mouseY, mouseClicked);
	}

	@Override
	public void draw() {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();

		renderUtils.drawOutlinedBox(parentX + 1, parentY + 15 + (this.getId() * 15), parentWidth - 2, 14, 0.3f, 0.3f,
				0.3f, 0.1f);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.text, parentX + 5,
				parentY + 19 + (this.getId() * 15), Minecraft.getInstance().aoba.hm.getColor(), true);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.popped ? ">>" : "<<", parentX + parentWidth - 12,
				parentY + 19 + (this.getId() * 15), Minecraft.getInstance().aoba.hm.getColor(), true);
		
		this.tabOpened.draw();
	}

	public boolean isPopped() {
		return this.popped;
	}
	
}
