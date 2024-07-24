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

package net.aoba.gui.navigation.windows;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.components.*;
import net.aoba.gui.navigation.Window;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HudOptionsTab extends Window {
    public HudOptionsTab() {
        super("Hud Options", 600, 200, 260, 50);

        StackPanelComponent stackPanel = new StackPanelComponent(this);
        stackPanel.setMargin(new Margin(null, 30f, null, null));

        List<String> fontNames = new ArrayList<String>();
        ConcurrentHashMap<String, TextRenderer> fontRenderers = Aoba.getInstance().fontManager.fontRenderers;
        Set<String> set = fontRenderers.keySet();

        for (String s : set) {
            fontNames.add(s);
        }

        // Keybinds Header
        stackPanel.addChild(new StringComponent("Keybinds", stackPanel, GuiManager.foregroundColor.getValue(), true));

        KeybindComponent clickGuiKeybindComponent = new KeybindComponent(stackPanel, Aoba.getInstance().hudManager.clickGuiButton);
        clickGuiKeybindComponent.setSize(new Rectangle(null, null, null, 30f));
        
        stackPanel.addChild(clickGuiKeybindComponent);

        // Hud Font Header
        stackPanel.addChild(new StringComponent("HUD Font", stackPanel, GuiManager.foregroundColor.getValue(), true));

        ListComponent listComponent = new ListComponent(stackPanel, fontNames, Aoba.getInstance().fontManager.fontSetting);
        stackPanel.addChild(listComponent);

        stackPanel.addChild(new StringComponent("HUD Colors", stackPanel, GuiManager.foregroundColor.getValue(), true));

        stackPanel.addChild(new ColorPickerComponent(stackPanel, GuiManager.foregroundColor));
        stackPanel.addChild(new ColorPickerComponent(stackPanel, GuiManager.backgroundColor));
        stackPanel.addChild(new ColorPickerComponent(stackPanel, GuiManager.borderColor));

        stackPanel.addChild(new StringComponent("Hud Styling", stackPanel, GuiManager.foregroundColor.getValue(), true));

        stackPanel.addChild(new SliderComponent(stackPanel, GuiManager.roundingRadius));

        stackPanel.addChild(new StringComponent("GUI / HUD Responsiveness", stackPanel, GuiManager.foregroundColor.getValue(), true));

        stackPanel.addChild(new SliderComponent(stackPanel, GuiManager.dragSmoothening));

        this.children.add(stackPanel);
        this.setWidth(300);
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);
    }
}
