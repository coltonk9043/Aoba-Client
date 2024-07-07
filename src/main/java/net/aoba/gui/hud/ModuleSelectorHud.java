/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.GuiManager;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.module.Module.Category;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ModuleSelectorHud extends AbstractHud {
    private KeyBinding keybindUp;
    private KeyBinding keybindDown;
    private KeyBinding keybindLeft;
    private KeyBinding keybindRight;

<<<<<<< Updated upstream
    private AobaClient aoba;
=======
	private AobaClient aoba;

	int index = 0;
	int indexMods = 0;
	boolean isCategoryMenuOpen = false;
>>>>>>> Stashed changes

    int index = 0;
    int indexMods = 0;
    boolean isCategoryMenuOpen = false;

    Category[] categories;
    ArrayList<Module> modules = new ArrayList<Module>();

<<<<<<< Updated upstream
    public ModuleSelectorHud() {
        super("ModuleSelectorHud", 0, 0, 150, 30);
        this.keybindUp = new KeyBinding("key.tabup", GLFW.GLFW_KEY_UP, "key.categories.aoba");
        this.keybindDown = new KeyBinding("key.tabdown", GLFW.GLFW_KEY_DOWN, "key.categories.aoba");
        this.keybindLeft = new KeyBinding("key.tableft", GLFW.GLFW_KEY_LEFT, "key.categories.aoba");
        this.keybindRight = new KeyBinding("key.tabright", GLFW.GLFW_KEY_RIGHT, "key.categories.aoba");

        categories = Module.Category.values();

        this.aoba = Aoba.getInstance();
    }

    @Override
    public void update() {
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
                if (!isCategoryMenuOpen) {
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
    public void draw(DrawContext drawContext, float partialTicks) {
        // Gets the client and window.
        MinecraftClient mc = MinecraftClient.getInstance();
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        Window window = mc.getWindow();

        Vector2 pos = position.getValue();

        // Draws the top bar including "Aoba x.x"
        RenderUtils.drawString(drawContext, "Aoba " + AobaClient.VERSION, 8, 8, GuiManager.foregroundColor.getValue());

        // Draws the table including all of the categories.
        RenderUtils.drawRoundedBox(matrix4f, pos.x, pos.y, width, height * this.categories.length, 6f, GuiManager.backgroundColor.getValue());
        RenderUtils.drawRoundedOutline(matrix4f, pos.x, pos.y, width, height * this.categories.length, 6f, GuiManager.borderColor.getValue());

        // For every category, draw a cell for it.
        for (int i = 0; i < this.categories.length; i++) {
            RenderUtils.drawString(drawContext, ">>", pos.x + width - 24, pos.y + (height * i) + 8, GuiManager.foregroundColor.getValue());
            // Draws the name of the category dependent on whether it is selected.
            if (this.index == i) {
                RenderUtils.drawString(drawContext, "> " + this.categories[i].name(), pos.x + 8, pos.y + (height * i) + 8, GuiManager.foregroundColor.getValue());
            } else {
                RenderUtils.drawString(drawContext, this.categories[i].name(), pos.x + 8, pos.y + (height * i) + 8, 0xFFFFFF);
            }
        }

        // If any particular category menu is open.
        if (isCategoryMenuOpen) {
            // Draw the table underneath
            RenderUtils.drawRoundedBox(matrix4f, pos.x + width, pos.y + (height * this.index), 165, height * modules.size(), 6f, GuiManager.backgroundColor.getValue());
            RenderUtils.drawRoundedOutline(matrix4f, pos.x + width, pos.y + (height * this.index), 165, height * modules.size(), 6f, GuiManager.borderColor.getValue());

            // For every mod, draw a cell for it.
            for (int i = 0; i < modules.size(); i++) {
                if (this.indexMods == i) {
                    RenderUtils.drawString(drawContext, "> " + modules.get(i).getName(), pos.x + width + 5,
                            pos.y + (i * height) + (this.index * height) + 8,
                            modules.get(i).getState() ? 0x00FF00 : GuiManager.foregroundColor.getValue().getColorAsInt());
                } else {
                    RenderUtils.drawString(drawContext, modules.get(i).getName(), pos.x + width + 5,
                            pos.y + (i * height) + (this.index * height) + 8,
                            modules.get(i).getState() ? 0x00FF00 : 0xFFFFFF);
                }
            }
        }
    }
=======
		categories = Module.Category.values();

		this.aoba = Aoba.getInstance();
	}

	@Override
	public void update() {
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
			if (!isCategoryMenuOpen) {
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

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		// Gets the client and window.
		MinecraftClient mc = MinecraftClient.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		Window window = mc.getWindow();

		Vector2 pos = position.getValue();

		// Draws the top bar including "Aoba x.x"
		RenderUtils.drawString(drawContext, "Aoba " + AobaClient.VERSION, 8, 8, GuiManager.foregroundColor.getValue());

		// Draws the table including all of the categories.
		RenderUtils.drawRoundedBox(matrix4f, pos.x, pos.y, width, height * this.categories.length, 6f,
				GuiManager.backgroundColor.getValue());
		RenderUtils.drawRoundedOutline(matrix4f, pos.x, pos.y, width, height * this.categories.length, 6f,
				GuiManager.borderColor.getValue());

		// For every category, draw a cell for it.
		for (int i = 0; i < this.categories.length; i++) {
			RenderUtils.drawString(drawContext, ">>", pos.x + width - 24, pos.y + (height * i) + 8,
					GuiManager.foregroundColor.getValue());
			// Draws the name of the category dependent on whether it is selected.
			if (this.index == i) {
				RenderUtils.drawString(drawContext, "> " + this.categories[i].name(), pos.x + 8,
						pos.y + (height * i) + 8, GuiManager.foregroundColor.getValue());
			} else {
				RenderUtils.drawString(drawContext, this.categories[i].name(), pos.x + 8, pos.y + (height * i) + 8,
						0xFFFFFF);
			}
		}

		// If any particular category menu is open.
		if (isCategoryMenuOpen) {
			// Draw the table underneath
			RenderUtils.drawRoundedBox(matrix4f, pos.x + width, pos.y + (height * this.index), 165,
					height * modules.size(), 6f, GuiManager.backgroundColor.getValue());
			RenderUtils.drawRoundedOutline(matrix4f, pos.x + width, pos.y + (height * this.index), 165,
					height * modules.size(), 6f, GuiManager.borderColor.getValue());

			// For every mod, draw a cell for it.
			for (int i = 0; i < modules.size(); i++) {
				if (this.indexMods == i) {
					RenderUtils.drawString(drawContext, "> " + modules.get(i).getName(), pos.x + width + 5,
							pos.y + (i * height) + (this.index * height) + 8, modules.get(i).getState() ? 0x00FF00
									: GuiManager.foregroundColor.getValue().getColorAsInt());
				} else {
					RenderUtils.drawString(drawContext, modules.get(i).getName(), pos.x + width + 5,
							pos.y + (i * height) + (this.index * height) + 8,
							modules.get(i).getState() ? 0x00FF00 : 0xFFFFFF);
				}
			}
		}
	}
>>>>>>> Stashed changes
}
