package net.aoba.gui.elements;

import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class CheckboxComponent extends Component {
	private String text;
	private boolean wasClicked = false;
	
	BooleanSetting checkbox;
	
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
		MinecraftClient mc = MinecraftClient.getInstance();
		if (HudManager.currentGrabbed == null) {
			if (mouseClicked) {
				if (mouseX >= ((parentX + parent.getWidth() - 28))
						&& mouseX <= ((parentX + parentWidth - 8))) {
					if (mouseY >= (((parentY + offset + 2)))
							&& mouseY <= (parentY + offset + 22)) {
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
	public void draw(int offset, MatrixStack matrixStack, float partialTicks, Color color) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, this.text, parentX + 5,
				parentY + offset + 4, 0xFFFFFF);
		if(this.checkbox.getValue()) {
			renderUtils.drawOutlinedBox(matrixStack, parentX + parentWidth - 14, parentY + 1 + offset, 10, 10, new Color(0.0f,0.6f,0.0f), 0.8f);
		}else {
			renderUtils.drawOutlinedBox(matrixStack, parentX + parentWidth - 14, parentY + 1 + offset, 10, 10, new Color(0.6f,0.0f,0.0f), 0.8f);
		}
	}
}
