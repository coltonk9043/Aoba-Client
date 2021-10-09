package net.aoba.gui.elements;

import java.util.ArrayList;
import java.util.List;

import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.settings.ListSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class ListComponent extends Component {

	private String text;
	private boolean wasClicked = false;
	private ListSetting list;
	private List<Component> settingsList = new ArrayList<Component>();
	
	public ListComponent(String text, ClickGuiTab parent) {
		super();
		this.text = text;
		this.parent = parent;
	}

	public ListComponent(ClickGuiTab parent, ListSetting list) {
		super();
		this.text = list.getName();
		this.parent = parent;
		this.list = list;
	}

	@Override
	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		MinecraftClient mc = MinecraftClient.getInstance();
		if (HudManager.currentGrabbed == null) {
			if (mouseClicked) {
				if (mouseY >= (((parentY + offset + 2))) && mouseY <= (parentY + offset + 22)) {
					// If Left arrow clicked.
					if (mouseX >= ((parentX + 5)) && mouseX <= ((parentX + 32))) {
						if (!this.wasClicked) {
							list.decrement();
							this.wasClicked = true;
							return;
						}
					}
					// If Right arrow clicked.
					if (mouseX >= ((parentX + parent.getWidth() - 32)) && mouseX <= ((parentX + parentWidth - 8))) {
						if (!this.wasClicked) {
							list.increment();
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
		int length = MinecraftClient.getInstance().textRenderer.getWidth(list.getValue()); 
		renderUtils.drawOutlinedBox(matrixStack, parentX + 2, parentY + offset, parentWidth - 8, 11, new Color(0.1f,0.1f,0.1f),
				0.3f);
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, list.getValue(), (parentX + (parentWidth / 2)) - length , parentY + offset + 2, 0xFFFFFF);
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack,">>", parentX + 4, parentY + offset + 2, 0xFFFFFF);
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack,"<<", parentX + 4 + (parentWidth - 17), parentY + offset + 2, 0xFFFFFF);
	}
}
