package aoba.main.gui.elements;

import aoba.main.gui.HudManager;
import aoba.main.gui.Tab;
import aoba.main.module.Module;
import net.minecraft.client.Minecraft;

public class ButtonComponent extends Component {

	private String text;
	private Module module;
	private Tab parent;
	private boolean wasClicked = false;

	public ButtonComponent(int id, String text, Tab parent, Module module) {
		super(id);
		this.text = text;
		this.parent = parent;
		this.module = module;
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
							module.toggle();
							this.wasClicked = true;
							return;
						}
					} else {
						if (this.wasClicked) {
							this.wasClicked = false;
						}
					}
				}
			}
		}
	}

	@Override
	public void draw() {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();

		renderUtils.drawOutlinedBox(parentX + 1, parentY + 15 + (this.getId() * 15), parentWidth - 2, 14, 0.3f, 0.3f,
				0.3f, 0.1f);
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.text, parentX + 5,
				parentY + 19 + (this.getId() * 15), module.getState() ? 0x00FF00 : 0xFFFFFF, true);
	}

}
