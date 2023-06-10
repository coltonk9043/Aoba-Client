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
 * AutoSoup Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.StewItem;
import net.minecraft.network.packet.Packet;

public class AutoSoup extends Module {

	public AutoSoup() {
		this.setName("AutoSoup");
		this.setBind(new KeyBinding("key.autosoup", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Automatically consumes soup when health is low (PvP only).");
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {
	
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		if(MC.player.getHealth() < 6) {
			int foodSlot= -1;
			for(int i = 0; i< 9; i++) {
				Item item = MC.player.getInventory().getStack(i).getItem();
				
				if(!(item instanceof StewItem)) {
					continue;
				}
			    MC.player.getInventory().selectedSlot = foodSlot;
			    MC.options.useKey.setPressed(true);
			    break;
			}
		}
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		
	}

	@Override
	public void onSendPacket(Packet<?> packet) {
		
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
	}
	
	public void setHunger(int hunger) {
		
	}
}