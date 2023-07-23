package net.aoba.gui.hud;

import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;
import net.aoba.settings.Settings;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.Color;
import net.aoba.module.Module;
import net.aoba.module.Module.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class ModuleSelectorHud extends AbstractHud {
	private KeyBinding keybindUp;
	private KeyBinding keybindDown;
	private KeyBinding keybindLeft;
	private KeyBinding keybindRight;

	private AobaClient aoba;
	
	int index = 0;
	int indexMods = 0;
	boolean isCategoryMenuOpen = false;

	Category[] categories;
	ArrayList<Module> modules = new ArrayList<Module>();

	public ModuleSelectorHud() {
		super(Settings.getSettingInt("x"), Settings.getSettingInt("y"), 150, 30);
		this.keybindUp = new KeyBinding("key.tabup", GLFW.GLFW_KEY_UP, "key.categories.aoba");
		this.keybindDown = new KeyBinding("key.tabdown", GLFW.GLFW_KEY_DOWN, "key.categories.aoba");
		this.keybindLeft = new KeyBinding("key.tableft", GLFW.GLFW_KEY_LEFT, "key.categories.aoba");
		this.keybindRight = new KeyBinding("key.tabright", GLFW.GLFW_KEY_RIGHT, "key.categories.aoba");

		categories = Module.Category.values();
		this.aoba = Aoba.getInstance();
	}

	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		
			super.update(mouseX, mouseY, mouseClicked);
			
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
							for (Module module : aoba.moduleManager.modules) {
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

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		// Gets the client and window.
		MinecraftClient mc = MinecraftClient.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		Window window = mc.getWindow();
		
		// Draws the top bar including "Aoba x.x"
		renderUtils.drawString(drawContext, "Aoba " + AobaClient.VERSION, 8, 8, color);

		// Draws the table including all of the categories.
		renderUtils.drawRoundedBox(matrixStack, x, y, width, height * this.categories.length, 6f, new Color(30,30,30), 0.4f);
		renderUtils.drawRoundedOutline(matrixStack, x, y, width, height * this.categories.length, 6f, new Color(0,0,0), 0.8f);
		
		// For every category, draw a cell for it.
		for (int i = 0; i < this.categories.length; i++) {
			renderUtils.drawString(drawContext, ">>", x + width - 24, y + (height * i) + 8, color);
			// Draws the name of the category dependent on whether it is selected.
			if (this.index == i) {
				renderUtils.drawString(drawContext, "> " + this.categories[i].name(), x + 8, y + (height * i) + 8, color);
			} else {
				renderUtils.drawString(drawContext, this.categories[i].name(), x + 8, y + (height * i) + 8, 0xFFFFFF);
			}
		}
		
		// If any particular category menu is open.
		if (isCategoryMenuOpen) {
			// Draw the table underneath
			renderUtils.drawRoundedBox(matrixStack, x + width, y + (height * this.index), 165, height * modules.size(), 6f, new Color(30,30,30), 0.4f);
			renderUtils.drawRoundedOutline(matrixStack, x + width, y + (height * this.index), 165, height * modules.size(), 6f, new Color(0,0,0), 0.8f);
			
			// For every mod, draw a cell for it.
			for (int i = 0; i < modules.size(); i++) {
				if (this.indexMods == i) {
					renderUtils.drawString(drawContext, "> " + modules.get(i).getName(), x + width + 5,
							y + (i * height) + (this.index * height) + 8,
							modules.get(i).getState() ? 0x00FF00 : color.getColorAsInt());
				} else {
					renderUtils.drawString(drawContext, modules.get(i).getName(), x + width + 5,
							y + (i * height) + (this.index * height) + 8,
							modules.get(i).getState() ? 0x00FF00 : 0xFFFFFF);
				}
			}
		}
		
		// Draws the active mods in the top right of the screen.
		int iteration = 0;
		for(int i = 0; i < aoba.moduleManager.modules.size(); i++) {
			Module mod = aoba.moduleManager.modules.get(i);
			if(mod.getState()) {
				renderUtils.drawString(drawContext, mod.getName(),
						(float) (window.getWidth() - ((mc.textRenderer.getWidth(mod.getName()) + 5) * 2)), 10 + (iteration*20),
						color.getColorAsInt());
				iteration++;
			}
		}
	}
}
