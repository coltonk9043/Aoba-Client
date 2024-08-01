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

import java.util.ArrayList;

import net.aoba.gui.Margin;
import net.aoba.gui.components.HudComponent;
import net.aoba.gui.components.StackPanelComponent;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.navigation.Window;
import net.minecraft.client.gui.DrawContext;

public class ToggleHudsTab extends Window {
    public ToggleHudsTab(ArrayList<HudWindow> huds) {
        super("Toggle HUDs", 0, 0, 50, 50);

        StackPanelComponent stackPanel = new StackPanelComponent(this);
        stackPanel.setMargin(new Margin(null, 30f, null, null));

        for (HudWindow hud : huds) {
            HudComponent hudComponent = new HudComponent(hud.getID(), stackPanel, hud);
            stackPanel.addChild(hudComponent);
        }

        this.children.add(stackPanel);
        this.setWidth(300);
    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        super.draw(drawContext, partialTicks);
    }
}
