/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

package net.aoba.gui;

import net.aoba.Aoba;
import net.aoba.gui.tabs.Tab;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class ArmorHUD extends Tab{

	private ItemRenderer itemRenderer; 
	
	public ArmorHUD() {
		itemRenderer = mc.getItemRenderer();
		this.x = 300;
		this.y = 300;
		this.width = 64;
		this.height = 256;
	}
	
	@Override
	public void update(double mouseX, double mouseY, boolean mouseClicked) {
		if (Aoba.getInstance().hm.isClickGuiOpen()) {
			if (HudManager.currentGrabbed == null) {
				if (mouseX >= (x) && mouseX <= (x + width)) {
					if (mouseY >= (y) && mouseY <= (y + height)) {
						if (mouseClicked) {
							HudManager.currentGrabbed = this;
						}
					}
				}
			}
		}
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		DefaultedList<ItemStack> armors = mc.player.getInventory().armor;
		int yOff = 16;
		System.out.println(armors.get(0).getName().getString());
		for(ItemStack armor : armors) {
			if(armor.getItem() == Items.AIR) continue;
			drawContext.drawItem(armor, this.x, this.y + this.height - yOff);
			yOff += 16;
		}
	}
}
