package net.aoba.gui.elements;

import java.util.ArrayList;
import java.util.List;
import net.aoba.module.Module;
import net.aoba.gui.ClickGuiTab;
import net.aoba.gui.Color;
import net.aoba.gui.HudManager;
import net.aoba.settings.Setting;
import net.aoba.settings.*;
import net.aoba.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class ModuleComponent extends Component {

	private String text;
	private Module module;
	private ClickGuiTab parent;
	private boolean wasClicked = false;
	private boolean popped = false;

	private List<Component> settingsList = new ArrayList<Component>();

	public ModuleComponent(String text, ClickGuiTab parent, Module module) {
		super();
		this.text = text;
		this.parent = parent;
		this.module = module;
		for (Setting setting : this.module.getSettings()) {
			Component c;
			if (setting instanceof SliderSetting) {
				c = new SliderComponent(this.parent, (SliderSetting) setting);
			} else if (setting instanceof BooleanSetting){
				c = new CheckboxComponent(this.parent, (BooleanSetting) setting);
			}else if(setting instanceof ListSetting){
				c = new ListComponent(this.parent, (ListSetting) setting);
			}else {
				c = null;
			}
			settingsList.add(c);
		}
	}

	public void update(int offset, double mouseX, double mouseY, boolean mouseClicked) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();
		MinecraftClient mc = MinecraftClient.getInstance();
		if (this.popped) {
			this.setHeight(15 + (this.settingsList.size() * 15));
			int i = offset + 15;
			for (Component children : this.settingsList) {
				children.update(i, mouseX, mouseY, mouseClicked);
				i += children.getHeight();
			}
		} else {
			this.setHeight(15);
		}

		if (HudManager.currentGrabbed == null) {
			if (mouseClicked) {
				// Enable Module
				if (mouseX >= ((parentX + 2)) && mouseX <= (((parentX + 2)) + parentWidth - 34)) {
					if (mouseY >= parentY + offset && mouseY <= (parentY + offset + 28)) {
						if (!this.wasClicked) {
							module.toggle();
							this.wasClicked = true;
							return;
						}
					}
				// Open Options Menu
				}else if(mouseX >= (((parentX + 2) + parentWidth - 34)) && mouseX <= (((parentX + 2)) + parentWidth - 4)){
					if (mouseY >= (((parentY + offset))) && mouseY <= ((parentY + offset) + 28)) {
						if (!this.wasClicked) {
							this.popped = !this.popped;
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

		renderUtils.drawOutlinedBox(matrixStack, parentX + 2, parentY + offset, parentWidth - 4, this.getHeight() - 2, new Color(0.5f,0.5f,0.5f), 0.2f);
		
		if(this.popped) {
			int i = offset + 15;
			for(Component children : this.settingsList) {
				if(children.isVisible()) {
					children.draw(i, matrixStack, partialTicks, color);
					i += children.getHeight();
				}
			}
		}
		
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, this.text, parentX + 5,
				parentY + 4 + offset, module.getState() ? 0x00FF00 : 0xFFFFFF);
		if(module.hasSettings()) {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, this.popped ? "<<" : ">>", parentX + parentWidth - 15,
					parentY + 4 + offset, color.getColorAsInt());
		}
	}

}
