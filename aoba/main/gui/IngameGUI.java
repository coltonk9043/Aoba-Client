package aoba.main.gui;

import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;
import aoba.main.Aoba;
import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import aoba.main.settings.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class IngameGUI extends Tab {
	private KeyBinding keybindUp;
	private KeyBinding keybindDown;
	private KeyBinding keybindLeft;
	private KeyBinding keybindRight;

	int index = 0;
	int indexMods = 0;
	boolean isCategoryMenuOpen = false;
	boolean infoEnabled = false;

	Category[] categories;
	ArrayList<Module> modules = new ArrayList<Module>();

	public IngameGUI() {
		this.keybindUp = new KeyBinding("key.tabup", GLFW.GLFW_KEY_UP, "key.categories.aoba");
		this.keybindDown = new KeyBinding("key.tabdown", GLFW.GLFW_KEY_DOWN, "key.categories.aoba");
		this.keybindLeft = new KeyBinding("key.tableft", GLFW.GLFW_KEY_LEFT, "key.categories.aoba");
		this.keybindRight = new KeyBinding("key.tabright", GLFW.GLFW_KEY_RIGHT, "key.categories.aoba");

		categories = Module.Category.values();
		this.x = Settings.getSettingInt("x");
		this.y = Settings.getSettingInt("y");
		this.width = 75;
		this.height = 15;
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		{
			if (mc.aoba.hm.isClickGuiOpen()) {
				if (HudManager.currentGrabbed == null) {
					if (mouseX >= (x *  mc.gameSettings.guiScale) && mouseX <= (x + width) * mc.gameSettings.guiScale) {
						if (mouseY >= (y *  mc.gameSettings.guiScale) && mouseY <= (y + height) *  mc.gameSettings.guiScale) {
							if (mouseClicked) {
								HudManager.currentGrabbed = this;
							}
						}
					}
				}
			}
			
			Minecraft mc = Minecraft.getInstance();
			if (!mc.aoba.isGhosted()) {
				if (this.keybindUp.isPressed()) {
					if (!isCategoryMenuOpen) {
						if (index == 0) {
							index = categories.length - 1;
						} else {
							index -= 1;
						}
					} else {
						if (indexMods == 0) {
							indexMods = modules.size() - 1;
						} else {
							indexMods -= 1;
						}
					}
				} else if (this.keybindDown.isPressed()) {
					if (!isCategoryMenuOpen) {
						index = (index + 1) % categories.length;
					} else {
						indexMods = (indexMods + 1) % modules.size();
					}
				} else if (this.keybindRight.isPressed()) {
					if (!isCategoryMenuOpen && x != -width) {
						isCategoryMenuOpen = !isCategoryMenuOpen;
						if (modules.isEmpty()) {
							for (Module module : mc.aoba.mm.modules) {
								if (module.isCategory(this.categories[this.index])) {
									modules.add(module);
								}
							}
						}
					} else {
						modules.get(indexMods).toggle();
					}
				} else if (this.keybindLeft.isPressed()) {
					if (this.isCategoryMenuOpen) {
						this.indexMods = 0;
						this.modules.clear();
						this.isCategoryMenuOpen = false;
					}
				}
			}
		}
	}

	@Override
	public void draw(int scaledWidth, int scaledHeight, Color color) {
		Minecraft mc = Minecraft.getInstance();
		renderUtils.drawOutlinedBox(0, 0, width, 14, 0.3f, 0.3f, 0.3f, 0.4f);

		mc.fontRenderer.drawStringWithShadow("§l" + Aoba.NAME + " " + Aoba.VERSION, 4, 4, color.getColorAsInt(), false);
		renderUtils.drawOutlinedBox(x, y, width, height * this.categories.length, 0.3f, 0.3f, 0.3f, 0.4f);
		for (int i = 0; i < this.categories.length; i++) {
			mc.fontRenderer.drawStringWithShadow(">>", x + width - 12, y + (height * i) + 5, color.getColorAsInt(),
					true);
			if (this.index == i) {
				mc.fontRenderer.drawStringWithShadow("> " + this.categories[i].name(), x + 5, y + (height * i) + 5,
						color.getColorAsInt(), false);
			} else {
				mc.fontRenderer.drawStringWithShadow(this.categories[i].name(), x + 5, y + (height * i) + 5, 0xFFFFFF,
						false);
			}
		}
		if (isCategoryMenuOpen) {
			renderUtils.drawOutlinedBox(x + width, y + (height * this.index), 85, height * modules.size(), 0.3f,
					0.3f, 0.3f, 0.4f);
			for (int i = 0; i < modules.size(); i++) {
				if (this.indexMods == i) {
					mc.fontRenderer.drawStringWithShadow("> " + modules.get(i).getName(), x + width + 5,
							y + (i * height) + (this.index * height) + 5,
							modules.get(i).getState() ? 0x00FF00 : color.getColorAsInt(), false);
				} else {
					mc.fontRenderer.drawStringWithShadow(modules.get(i).getName(), x + width + 5,
							y + (i * height) + (this.index * height) + 5,
							modules.get(i).getState() ? 0x00FF00 : 0xFFFFFF, false);
				}
			}
		}
		int instances = 5;
		for (Module module : mc.aoba.mm.modules) {
			if (module.getState()) {
				mc.fontRenderer.drawStringWithShadow(module.getName(),
						(float) (scaledWidth - mc.fontRenderer.getStringWidth(module.getName()) - 5), instances,
						color.getColorAsInt(), true);
				instances += 10;
			}
		}
	}

	public void toggleInfoHud() {
		this.infoEnabled = !this.infoEnabled;
	}
}
