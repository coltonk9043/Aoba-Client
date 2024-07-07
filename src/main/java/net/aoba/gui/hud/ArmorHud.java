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

/**
 * A class to represent a Tab containing Armor Information
 */

package net.aoba.gui.hud;

import net.aoba.utils.types.Vector2;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class ArmorHud extends AbstractHud {

    public ArmorHud(int x, int y, int width, int height) {
        super("ArmorHud", x, y, width, height);
        this.width = 60;
        this.height = 256;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(DrawContext drawContext, float partialTicks) {
        if (this.visible) {
            DefaultedList<ItemStack> armors = mc.player.getInventory().armor;
            Vector2 pos = position.getValue();

            if (this.isDragging) {
                int highlightColor = 0x80FF0000;
                int x1 = (int) pos.x;
                int y1 = (int) pos.y;
                int x2 = (int) (pos.x + this.width);
                int y2 = (int) (pos.y + this.height);
                drawContext.fill(x1, y1, x2, y2, highlightColor);
            }

            int yOff = 16;
            for (ItemStack armor : armors) {
                if (armor.getItem() != Items.AIR) {
                    drawContext.drawItem(armor, (int) pos.x, (int) (pos.y + this.height - yOff));
                } else {
                    int placeholderColor = 0x40404040;
                    drawContext.fill((int) pos.x, (int) (pos.y + this.height - yOff), (int) (pos.x + this.width), (int) (pos.y + this.height - yOff + 16), placeholderColor);
                }
                yOff += 16;
            }
        }
    }
}
