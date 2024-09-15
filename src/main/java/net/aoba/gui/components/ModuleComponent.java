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

package net.aoba.gui.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.navigation.CloseableWindow;
import net.aoba.utils.render.Render2D;
import net.aoba.module.Module;
import net.aoba.settings.Setting;
import net.aoba.settings.types.BlocksSetting;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static net.aoba.utils.render.TextureBank.gear;

public class ModuleComponent extends Component {
    private String text;
    private Module module;

    private CloseableWindow lastSettingsTab = null;
    private boolean spinning = false;
    private float spinAngle = 0;

    public ModuleComponent(String text, IGuiElement parent, Module module) {
        super(parent, new Rectangle(null, null, null, 30f));

        this.text = text;
        this.module = module;

        this.setMargin(new Margin(8f, null, 8f, null));
    }

    @Override
    public void update() {
        super.update();
        if (spinning) {
            spinAngle = (spinAngle + 5) % 360;
        }
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);

        MatrixStack matrixStack = drawContext.getMatrices();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        float actualX = this.getActualSize().getX();
        float actualY = this.getActualSize().getY();
        float actualWidth = this.getActualSize().getWidth();
        
        Render2D.drawString(drawContext, this.text, actualX, actualY + 8, module.getState() ? 0x00FF00 : this.hovered ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);
        if (module.hasSettings()) {
            Color hudColor = GuiManager.foregroundColor.getValue();

            if (spinning) {
                matrixStack.push();
                matrixStack.translate((actualX + actualWidth - 8), (actualY + 14), 0);
                matrixStack.multiply(new Quaternionf().rotateZ((float) Math.toRadians(spinAngle)));
                matrixStack.translate(-(actualX + actualWidth - 8), -(actualY + 14), 0);
                Render2D.drawTexturedQuad(matrixStack.peek().getPositionMatrix(), gear, (actualX + actualWidth - 16), (actualY + 6), 16, 16, hudColor);
                matrixStack.pop();
            } else {
            	Render2D.drawTexturedQuad(matrix4f, gear, (actualX + actualWidth - 16), (actualY + 6), 16, 16, hudColor);
            }
        }
    }

    @Override
    public void onMouseClick(MouseClickEvent event) {
    	super.onMouseClick(event);
    	
        if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
            if (hovered) {
            	float mouseX = (float)event.mouseX;
            	float actualX = actualSize.getX();
                float actualY = actualSize.getY();
                float actualWidth = actualSize.getWidth();
                
                boolean isOnOptionsButton = (mouseX >= (actualX + actualWidth - 34) && mouseX <= (actualX + actualWidth));
                if (isOnOptionsButton) {
                    spinning = true;
                    if (lastSettingsTab == null) {
                        lastSettingsTab = new CloseableWindow(this.module.getName(), actualX + actualWidth + 1, actualY, 320.0f, 0.0f);
                        lastSettingsTab.minWidth = 320.0f;
                        lastSettingsTab.setInheritHeightFromChildren(true);
                        StackPanelComponent stackPanel = new StackPanelComponent(lastSettingsTab);
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

                        lastSettingsTab.addChild(stackPanel);
                        
                        lastSettingsTab.setOnClose(() -> {spinning = false;});
                        
                        lastSettingsTab.setVisible(true);
                        Aoba.getInstance().hudManager.AddWindow(lastSettingsTab, "Modules");
                        spinning = true;
                    } else {
                        Aoba.getInstance().hudManager.RemoveWindow(lastSettingsTab, "Modules");
                        spinning = false;
                        lastSettingsTab = null;
                    }
                } else {
                    module.toggle();
                }
                
                event.cancel();
            }
        }
    }
    
    public String getTooltip() {
		return module.getDescription();
	}
}
