package aoba.main.gui.elements;

import aoba.main.gui.HudManager;

import java.util.ArrayList;
import java.util.List;

import aoba.main.gui.ClickGuiTab;
import aoba.main.module.Module;
import aoba.main.settings.BooleanSetting;
import aoba.main.settings.Setting;
import aoba.main.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import aoba.main.gui.Color;

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
		Minecraft mc = Minecraft.getInstance();
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
				if (mouseX >= ((parentX + 1) * mc.gameSettings.guiScale) && mouseX <= (((parentX + 1)) + parentWidth - 17) * mc.gameSettings.guiScale) {
					if (mouseY >= (((parentY + offset)) * mc.gameSettings.guiScale) && mouseY <= ((parentY + offset) + 14) * mc.gameSettings.guiScale) {
						if (!this.wasClicked) {
							module.toggle();
							this.wasClicked = true;
							return;
						}
					}
				// Open Options Menu
				}else if(mouseX >= (((parentX + 1) + parentWidth - 17) * mc.gameSettings.guiScale) && mouseX <= (((parentX + 1)) + parentWidth - 2) * mc.gameSettings.guiScale){
					if (mouseY >= (((parentY + offset)) * mc.gameSettings.guiScale) && mouseY <= ((parentY + offset) + 14) * mc.gameSettings.guiScale) {
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
	public void draw(int offset, int scaledWidth, int scaledHeight, Color color) {
		int parentX = parent.getX();
		int parentY = parent.getY();
		int parentWidth = parent.getWidth();

		renderUtils.drawOutlinedBox(parentX + 1, parentY + offset, parentWidth - 2, this.getHeight() - 1, 0.5f, 0.5f,
				0.5f, 0.2f);
		
		if(this.popped) {
			int i = offset + 15;
			for(Component children : this.settingsList) {
				children.draw(i, scaledWidth, scaledHeight, color);
				i += children.getHeight();
			}
		}
		
		Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.text, parentX + 5,
				parentY + 4 + offset, module.getState() ? 0x00FF00 : 0xFFFFFF, true);
		
		if(module.hasSettings()) {
			Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.popped ? "<<" : ">>", parentX + parentWidth - 15,
					parentY + 4 + offset, color.getColorAsInt(), true);
		}
	}

}
