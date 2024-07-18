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
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.tabs.components.*;
import net.aoba.misc.Render2D;
import net.aoba.misc.Render3D;
import net.aoba.module.Module;
import net.aoba.settings.Setting;
import net.aoba.settings.types.*;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class ModuleSettingsTab extends AbstractTab {
    protected String title;
    protected Module module;

    public ModuleSettingsTab(String title, float x, float y, Module module) {
        super(title + " Settings", x, y, 350.0f, 0.0f, false);
        this.module = module;

        StackPanelComponent stackPanel = new StackPanelComponent(this);
        stackPanel.setMargin(new Margin(null, 30f, null, null));

        KeybindComponent keybindComponent = new KeybindComponent(stackPanel, module.getBind());
        keybindComponent.setSize(new Rectangle(null, null, null, 30f));

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

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);
        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        Rectangle pos = position.getValue();

        Render2D.drawLine(matrix4f, pos.getX() + pos.getWidth() - 23, pos.getY() + 8, pos.getX() + pos.getWidth() - 8, pos.getY() + 23, new Color(255, 0, 0, 255));
        Render2D.drawLine(matrix4f, pos.getX() + pos.getWidth() - 23, pos.getY() + 23, pos.getX() + pos.getWidth() - 8, pos.getY() + 8, new Color(255, 0, 0, 255));
    }

    @Override
    public void OnMouseClick(MouseClickEvent event) {
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            double mouseX = mc.mouse.getX();
            double mouseY = mc.mouse.getY();
            Rectangle pos = position.getValue();

            if (Aoba.getInstance().hudManager.isClickGuiOpen()) {
                if (mouseX >= (pos.getX() + pos.getWidth() - 24) && mouseX <= (pos.getX() + pos.getWidth() - 2)) {
                    if (mouseY >= (pos.getY() + 4) && mouseY <= (pos.getY() + 20)) {
                        Aoba.getInstance().hudManager.RemoveHud(this, "Modules");
                        event.cancel();
                        return;
                    }
                }
            }
        }

        // If we did not hit the X, perform the regular AbstractGUI mouse click logic.
        super.OnMouseClick(event);
    }

    public void preupdate() {
    }

    public void postupdate() {
    }
}
