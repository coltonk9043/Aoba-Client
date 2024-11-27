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

package net.aoba.gui.navigation.huds;

import net.aoba.gui.Rectangle;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class ArmorHud extends HudWindow {

	public ArmorHud(int x, int y) {
		super("ArmorHud", x, y);
		this.minHeight = 256f;
		this.maxHeight = 256f;
		this.minWidth = 16f;
		this.maxWidth = 16f;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		if (isVisible()) {
			Rectangle pos = position.getValue();

			if (pos.isDrawable()) {
				DefaultedList<ItemStack> armors = MC.player.getInventory().armor;

				float scale = this.getActualSize().getHeight() / 64.0f;

				float x1 = pos.getX() / scale;
				float y2 = (pos.getY() + pos.getHeight()) / scale;
				float yOff = 0;
				MatrixStack matrixStack = drawContext.getMatrices();
				matrixStack.push();
				matrixStack.scale(scale, scale, scale);

				for (ItemStack armor : armors) {
					if (armor.getItem() != Items.AIR) {
						Render2D.drawItem(drawContext, armor, x1, y2 - yOff - 16);
					}
					yOff += (16.0f);
				}
				matrixStack.pop();
			}
		}
		super.draw(drawContext, partialTicks);
	}
}
