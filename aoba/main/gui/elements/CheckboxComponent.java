package aoba.main.gui.elements;

import aoba.main.gui.ClickGuiTab;
import aoba.main.gui.Color;
import aoba.main.gui.HudManager;
import aoba.main.settings.BooleanSetting;
import net.minecraft.client.Minecraft;

public class CheckboxComponent extends Component {
	private String text;
	private boolean wasClicked = false;
	
	BooleanSetting checkbox;
	
	public CheckboxComponent(String text, ClickGuiTab parent) {
		super();
		this.text = text;
		this.parent = parent;
	}
	
	public CheckboxComponent(ClickGuiTab parent, BooleanSetting checkbox) {
		super();
		this.text = checkbox.getName();
		this.parent = parent;
		this.checkbox = checkbox;
	}

	@Override
	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		Minecraft mc = Minecraft.getInstance();
		if (HudManager.currentGrabbed == null) {
			if (mouseClicked) {
				if (mouseX >= ((parentX + parent.getWidth() - 14) * mc.gameSettings.guiScale)
						&& mouseX <= ((parentX + parentWidth - 4)) * mc.gameSettings.guiScale) {
					if (mouseY >= (((parentY + offset + 1)) * mc.gameSettings.guiScale)
							&& mouseY <= (parentY + offset + 11) * mc.gameSettings.guiScale) {
						if (!this.wasClicked) {
							checkbox.toggleValue();
							this.wasClicked = true;
							return;
						}
					}
				}
			} else {
				if (this.wasClicked) {
					this.wasClicked = false;
				}
			}
		}
	}

	@Override
	public void draw(int offset, int scaledWidth, int scaledHeight, Color color) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.text, parentX + 5,
				parentY + offset + 3, 0xFFFFFF, true);
		if(this.checkbox.getValue()) {
			renderUtils.drawOutlinedBox(parentX + parentWidth - 14, parentY + 1 + offset, 10, 10, 0.0f,
					0.6f, 0.0f, 0.8f);
		}else {
			renderUtils.drawOutlinedBox(parentX + parentWidth - 14, parentY + 1 + offset, 10, 10, 0.6f,
					0.0f, 0.0f, 0.8f);
		}
	}
}
