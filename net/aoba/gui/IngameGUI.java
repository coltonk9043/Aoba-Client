package net.aoba.gui;

import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;
import net.aoba.settings.Settings;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.Module;
import net.aoba.module.Module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class IngameGUI extends Tab {
	private KeyBinding keybindUp;
	private KeyBinding keybindDown;
	private KeyBinding keybindLeft;
	private KeyBinding keybindRight;

	private AobaClient aoba;
	
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
		this.aoba = Aoba.getInstance();
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		{
			
			if (aoba.hm.isClickGuiOpen()) {
				if (HudManager.currentGrabbed == null) {
					if (mouseX >= (x) && mouseX <= (x + width)) {
						if (mouseY >= (y) && mouseY <= (y + height)) {
							if (mouseClicked) {
								HudManager.currentGrabbed = this;
							}
						}
					}
				}
			}
			if (!aoba.isGhosted()) {
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
					this.keybindUp.setPressed(false);
				} else if (this.keybindDown.isPressed()) {
					if (!isCategoryMenuOpen) {
						index = (index + 1) % categories.length;
					} else {
						indexMods = (indexMods + 1) % modules.size();
					}
					this.keybindDown.setPressed(false);
				} else if (this.keybindRight.isPressed()) {
					if (!isCategoryMenuOpen && x != -width) {
						isCategoryMenuOpen = !isCategoryMenuOpen;
						if (modules.isEmpty()) {
							for (Module module : aoba.mm.modules) {
								if (module.isCategory(this.categories[this.index])) {
									modules.add(module);
								}
							}
						}
					} else {
						modules.get(indexMods).toggle();
					}
					this.keybindRight.setPressed(false);
				} else if (this.keybindLeft.isPressed()) {
					if (this.isCategoryMenuOpen) {
						this.indexMods = 0;
						this.modules.clear();
						this.isCategoryMenuOpen = false;
					}
					this.keybindLeft.setPressed(false);
				}
			}
		}
	}

	@Override
	public void draw(MatrixStack matrixStack, float partialTicks, Color color) {
		MinecraftClient mc = MinecraftClient.getInstance();
		Window window = mc.getWindow();
		
		renderUtils.drawOutlinedBox(matrixStack, 0, 0, width, 15, new Color(0.3f,0.3f,0.3f), 1f);

		mc.textRenderer.drawWithShadow(matrixStack, "§lAoba V" + AobaClient.VERSION, 4, 4, color.getColorAsInt(), false);
		renderUtils.drawOutlinedBox(matrixStack, x, y, width, height * this.categories.length, new Color(0.3f,0.3f,0.3f), 0.4f);
		for (int i = 0; i < this.categories.length; i++) {
			mc.textRenderer.drawWithShadow(matrixStack, ">>", x + width - 12, y + (height * i) + 5, color.getColorAsInt(),
					true);
			if (this.index == i) {
				mc.textRenderer.drawWithShadow(matrixStack, "> " + this.categories[i].name(), x + 5, y + (height * i) + 5,
						color.getColorAsInt(), false);
			} else {
				mc.textRenderer.drawWithShadow(matrixStack, this.categories[i].name(), x + 5, y + (height * i) + 5, 0xFFFFFF,
						false);
			}
		}
		if (isCategoryMenuOpen) {
			renderUtils.drawOutlinedBox(matrixStack, x + width, y + (height * this.index), 125, height * modules.size(), new Color(0.3f,0.3f,0.3f), 0.4f);
			for (int i = 0; i < modules.size(); i++) {
				if (this.indexMods == i) {
					mc.textRenderer.drawWithShadow(matrixStack, "> " + modules.get(i).getName(), x + width + 5,
							y + (i * height) + (this.index * height) + 5,
							modules.get(i).getState() ? 0x00FF00 : color.getColorAsInt(), false);
				} else {
					mc.textRenderer.drawWithShadow(matrixStack, modules.get(i).getName(), x + width + 5,
							y + (i * height) + (this.index * height) + 5,
							modules.get(i).getState() ? 0x00FF00 : 0xFFFFFF, false);
				}
			}
		}
		int instances = 10;
		for (Module module : aoba.mm.modules) {
			if (module.getState()) {
				mc.textRenderer.drawWithShadow(matrixStack, module.getName(),
						(float) (window.getHeight() - ((mc.textRenderer.getWidth(module.getName()) + 5) * 2)), instances,
						color.getColorAsInt(), true);
				instances += 20;
			}
		}
	}

	public void toggleInfoHud() {
		this.infoEnabled = !this.infoEnabled;
	}
}
