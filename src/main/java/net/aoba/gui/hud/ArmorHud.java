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

package net.aoba.gui.hud;

import net.aoba.Aoba;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.gui.AbstractGui;
import net.aoba.gui.Color;
import net.aoba.gui.GuiManager;
import net.aoba.utils.types.Vector2;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class ArmorHud extends AbstractHud{

	public ArmorHud(int x, int y, int width, int height) {
		super("ArmorHud", x,y,width,height);
		this.width = 60;
		this.height = 256;
	}

	@Override
	public void update() {
		
	}
	
	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		if(this.visible) {
			DefaultedList<ItemStack> armors = mc.player.getInventory().armor;
			
			Vector2 pos = position.getValue();
			int yOff = 16;
			for(ItemStack armor : armors) {
				if(armor.getItem() == Items.AIR) continue;
				drawContext.drawItem(armor, (int)pos.x, (int)(pos.y + this.height - yOff));
				yOff += 16;
			}
		}
	}
	
	@Override
	public void OnLeftMouseDown(LeftMouseDownEvent event) {
		super.OnLeftMouseDown(event);

		if(this.isMouseOver) {
			GuiManager.currentGrabbed = this;
		}
	}
}
