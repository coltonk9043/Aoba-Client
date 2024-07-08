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

package net.aoba.gui.tabs;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.AbstractGui;
import net.aoba.gui.GuiManager;
import net.aoba.gui.colors.Color;
import net.aoba.gui.tabs.components.*;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.Setting;
import net.aoba.settings.types.*;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class ModuleSettingsTab extends AbstractGui {
    protected String title;
    protected Module module;

    public ModuleSettingsTab(String title, float x, float y, Module module) {
        super(title + "_tab", x, y, 180, 0);
        this.title = title + " Settings";
        this.module = module;
        this.setWidth(280);

        StackPanelComponent stackPanel = new StackPanelComponent(this);
        stackPanel.setTop(30);

        KeybindComponent keybindComponent = new KeybindComponent(stackPanel, module.getBind());
        keybindComponent.setHeight(30);
        stackPanel.addChild(keybindComponent);

        for (Setting<?> setting : this.module.getSettings()) {
            Component c;
            if (setting instanceof FloatSetting) {
                c = new SliderComponent(stackPanel, (FloatSetting) setting);
            } else if (setting instanceof BooleanSetting) {
                c = new CheckboxComponent(stackPanel, (BooleanSetting) setting);
                //}else if (setting instanceof StringListSetting) {
                //c = new ListComponent(stackPanel, (IndexedStringListSetting) setting);
            } else if (setting instanceof ColorSetting) {
                c = new ColorPickerComponent(stackPanel, (ColorSetting) setting);
            } else if (setting instanceof BlocksSetting) {
                c = new BlocksComponent(stackPanel, (BlocksSetting) setting);
            } else if (setting instanceof EnumSetting) {
                c = new EnumComponent<>(stackPanel, (EnumSetting) setting);
            } else {
                c = null;
            }

            if (c != null) {
                stackPanel.addChild(c);
            }
        }

        this.addChild(stackPanel);
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {

        this.title = title;
    }

    public final void addChild(Component component) {
        this.children.add(component);
    }

    @Override
    public void update() {
        if (this.inheritHeightFromChildren) {
            float tempHeight = 0;
            for (Component child : children) {
                tempHeight += (child.getHeight());
            }
            this.setHeight(tempHeight);
        }

        if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
            for (Component child : this.children) {
                child.update();
            }
        }
    }

    public void preupdate() {
    }

    public void postupdate() {
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        Vector2 pos = position.getValue();

        Color backgroundColor = new Color(0, 0, 0, 200); // Half-transparent black

        RenderUtils.drawRoundedBox(matrix4f, pos.x, pos.y, width, height + 30, 6, backgroundColor);
        RenderUtils.drawRoundedOutline(matrix4f, pos.x, pos.y, width, height + 30, 6, backgroundColor);
        RenderUtils.drawString(drawContext, this.title, pos.x + 8, pos.y + 8, GuiManager.foregroundColor.getValue());
        RenderUtils.drawLine(matrix4f, pos.x, pos.y + 30, pos.x + width, pos.y + 30, new Color(0, 0, 0, 100));
        RenderUtils.drawLine(matrix4f, pos.x + width - 23, pos.y + 8, pos.x + width - 8, pos.y + 23, new Color(255, 0, 0, 255));
        RenderUtils.drawLine(matrix4f, pos.x + width - 23, pos.y + 23, pos.x + width - 8, pos.y + 8, new Color(255, 0, 0, 255));

        // Draw child components
        for (Component child : children) {
            child.draw(drawContext, partialTicks);
        }
    }

    @Override
    public void OnMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            double mouseX = mc.mouse.getX();
            double mouseY = mc.mouse.getY();
            Vector2 pos = position.getValue();

            if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
                if (mouseX >= pos.x && mouseX <= pos.x + width) {
                    if (mouseY >= pos.y && mouseY <= pos.y + 24) {
                        this.lastClickOffsetX = mouseX - pos.x;
                        this.lastClickOffsetY = mouseY - pos.y;
                        GuiManager.currentGrabbed = this;
                    }
                }

                if (mouseX >= (pos.x + width - 24) && mouseX <= (pos.x + width - 2)) {
                    if (mouseY >= (pos.y + 4) && mouseY <= (pos.y + 20)) {
                        GuiManager.currentGrabbed = null;
                        Aoba.getInstance().hudManager.RemoveHud(this, "Modules");
                    }
                }
            }
        }
    }
}
