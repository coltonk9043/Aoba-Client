/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.aoba.AobaClient;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.ResizeMode;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;

public class ModuleSelectorHud extends HudWindow {

	private static final float ROW_HEIGHT = 30.0f;

	private final KeyMapping keybindUp;
	private final KeyMapping keybindDown;
	private final KeyMapping keybindLeft;
	private final KeyMapping keybindRight;

	private int index = 0;
	private int indexMods = 0;
	private boolean isCategoryMenuOpen = false;

	private final List<Category> categories = new ArrayList<>();
	private final ArrayList<Module> modules = new ArrayList<Module>();

	public ModuleSelectorHud() {
		super("ModuleSelectorHud", 0, 0);

		categories.addAll(Category.getAllCategories().values());

		setMinWidth(180.0f);
		float newHeight = categories.size() * ROW_HEIGHT;
		setMinHeight(newHeight);
		setHeight(newHeight);

		resizeMode = ResizeMode.Width;

		keybindUp = new KeyMapping("key.tabup", GLFW.GLFW_KEY_UP, AobaClient.AOBA_CATEGORY);
		keybindDown = new KeyMapping("key.tabdown", GLFW.GLFW_KEY_DOWN, AobaClient.AOBA_CATEGORY);
		keybindLeft = new KeyMapping("key.tableft", GLFW.GLFW_KEY_LEFT, AobaClient.AOBA_CATEGORY);
		keybindRight = new KeyMapping("key.tabright", GLFW.GLFW_KEY_RIGHT, AobaClient.AOBA_CATEGORY);
	}

	@Override
	public void update() {
		if (keybindUp.isDown()) {
			if (!isCategoryMenuOpen) {
				if (index == 0) {
					index = categories.size() - 1;
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
			keybindUp.setDown(false);
		} else if (keybindDown.isDown()) {
			if (!isCategoryMenuOpen) {
				index = (index + 1) % categories.size();
			} else {
				indexMods = (indexMods + 1) % modules.size();
			}
			keybindDown.setDown(false);
		} else if (keybindRight.isDown()) {
			if (!isCategoryMenuOpen) {
				isCategoryMenuOpen = true;
				if (modules.isEmpty()) {
					for (Module module : AOBA.moduleManager.modules) {
						if (module.isCategory(categories.get(index))) {
							modules.add(module);
						}
					}
				}
			} else {
				modules.get(indexMods).toggle();
			}
			keybindRight.setDown(false);
		} else if (keybindLeft.isDown()) {
			if (isCategoryMenuOpen) {
				indexMods = 0;
				modules.clear();
				isCategoryMenuOpen = false;
			}
			keybindLeft.setDown(false);
		}
	}

	@Override
	public void draw(GuiGraphics drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		// Gets the client and window.
		Rectangle pos = getActualSize();

		if (pos.isDrawable()) {
			float x = pos.getX().floatValue();
			float y = pos.getY().floatValue();
			float width = pos.getWidth().floatValue();
			float height = pos.getHeight().floatValue();

			// Draws background depending on components width and height
			Render2D.drawRoundedBox(drawContext, x, y, width, height, GuiManager.roundingRadius.getValue(),
					GuiManager.backgroundColor.getValue());
			Render2D.drawRoundedBoxOutline(drawContext, x, y, width, height, GuiManager.roundingRadius.getValue(),
					GuiManager.borderColor.getValue());

			// For every category, draw a cell for it.
			for (int i = 0; i < categories.size(); i++) {
				Render2D.drawString(drawContext, ">>", x + width - 24, y + (ROW_HEIGHT * i) + 8,
						GuiManager.foregroundColor.getValue());

				// Draws the name of the category dependent on whether it is selected.
				if (index == i)
					Render2D.drawString(drawContext, "> " + categories.get(i).getName(), x + 8,
							y + (ROW_HEIGHT * i) + 8, GuiManager.foregroundColor.getValue());
				else
					Render2D.drawString(drawContext, categories.get(i).getName(), x + 8, y + (ROW_HEIGHT * i) + 8,
							0xFFFFFF);
			}

			// If any particular category menu is open.
			if (isCategoryMenuOpen) {
				// Draw the table underneath
				Render2D.drawRoundedBox(drawContext, x + width, y + (ROW_HEIGHT * index), 165,
						ROW_HEIGHT * modules.size(), GuiManager.roundingRadius.getValue(),
						GuiManager.backgroundColor.getValue());
				Render2D.drawRoundedBoxOutline(drawContext, x + width, y + (ROW_HEIGHT * index), 165,
						ROW_HEIGHT * modules.size(), GuiManager.roundingRadius.getValue(),
						GuiManager.borderColor.getValue());

				// For every mod, draw a cell for it.
				for (int i = 0; i < modules.size(); i++) {
					if (indexMods == i) {
						Render2D.drawString(drawContext, "> " + modules.get(i).getName(), x + width + 5,
								y + (i * ROW_HEIGHT) + (index * ROW_HEIGHT) + 8,
								modules.get(i).state.getValue() ? 0x00FF00
										: GuiManager.foregroundColor.getValue().getColorAsInt());
					} else {
						Render2D.drawString(drawContext, modules.get(i).getName(), x + width + 5,
								y + (i * ROW_HEIGHT) + (index * ROW_HEIGHT) + 8,
								modules.get(i).state.getValue() ? 0x00FF00 : 0xFFFFFF);
					}
				}
			}
		}
	}
}