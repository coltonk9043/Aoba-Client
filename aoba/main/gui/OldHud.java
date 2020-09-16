package aoba.main.gui;

import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;
import aoba.main.Aoba;
import aoba.main.misc.RenderUtils;
import aoba.main.module.Module;
import aoba.main.module.Module.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class OldHud {

	private KeyBinding keybindUp;
	private KeyBinding keybindDown;
	private KeyBinding keybindLeft;
	private KeyBinding keybindRight;
	private RenderUtils renderUtils = new RenderUtils();

	public int x = 0;
	public int y = 18;
	public int width = 75;
	public int height = 15;

	int index = 0;
	int indexMods = 0;
	boolean isCategoryMenuOpen = false;
	boolean infoEnabled = false;

	Category[] categories;
	ArrayList<Module> modules = new ArrayList<Module>();

	public OldHud() {
		this.keybindUp = new KeyBinding("key.tabup", GLFW.GLFW_KEY_UP, "key.categories.aoba");
		this.keybindDown = new KeyBinding("key.tabdown", GLFW.GLFW_KEY_DOWN, "key.categories.aoba");
		this.keybindLeft = new KeyBinding("key.tableft", GLFW.GLFW_KEY_LEFT, "key.categories.aoba");
		this.keybindRight = new KeyBinding("key.tabright", GLFW.GLFW_KEY_RIGHT, "key.categories.aoba");

		categories = Module.Category.values();
	}

	public void update() {
		Minecraft mc = Minecraft.getInstance();
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

	public void draw(int scaledWidth, int scaledHeight) {
		Minecraft mc = Minecraft.getInstance();
		renderUtils.drawOutlinedBox(0, 0, width, 14, 0.3f, 0.3f, 0.3f, 0.65f);
		mc.fontRenderer.drawStringWithShadow(Aoba.NAME + " " + Aoba.VERSION, 8, 4, mc.aoba.hm.getColor(), false);
		renderUtils.drawOutlinedBox(x, y, width, height * this.categories.length, 0.3f, 0.3f, 0.3f, 0.65f);
		for (int i = 0; i < this.categories.length; i++) {
			mc.fontRenderer.drawStringWithShadow(">>", x + width - 12, y + (height * i) + 5,
					mc.aoba.hm.getColor(), true);
			if (this.index == i) {
				mc.fontRenderer.drawStringWithShadow("> " + this.categories[i].name(), x + 5,
						y + (height * i) + 5, mc.aoba.hm.getColor(), false);
			} else {
				mc.fontRenderer.drawStringWithShadow(this.categories[i].name(), x + 5,
						y + (height * i) + 5, 0xFFFFFF, false);
			}
		}
		if (isCategoryMenuOpen) {
			renderUtils.drawOutlinedBox(x + width, y + (height * this.index), width, height * modules.size(), 0.3f,
					0.3f, 0.3f, 0.65f);
			for (int i = 0; i < modules.size(); i++) {
				if (this.indexMods == i) {
					mc.fontRenderer.drawStringWithShadow("> " + modules.get(i).getName(),
							x + width + 5, y + (i * height) + (this.index * height) + 5,
							modules.get(i).getState() ? 0x00FF00 : mc.aoba.hm.getColor(), false);
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
				mc.fontRenderer.drawStringWithShadow(
						module.getName(), (float) (scaledWidth
								- mc.fontRenderer.getStringWidth(module.getName()) - 5),
						instances, mc.aoba.hm.getColor(), true);
				instances += 10;
			}
		}
	}
	public void toggleInfoHud() {
		this.infoEnabled = !this.infoEnabled;
	}
}
