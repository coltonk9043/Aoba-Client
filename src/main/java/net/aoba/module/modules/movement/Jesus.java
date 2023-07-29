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
 * Jesus Module
 */
package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;

import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;

public class Jesus extends Module {
	
	public BooleanSetting legit;
	
	public Jesus() {
		this.setName("Jesus");
		this.setBind(new KeyBinding("key.jesus", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to walk on water.");
		
		// Add Settings
		legit = new BooleanSetting("jesus_legit", "Legit", true, null);
		this.addSetting(legit);
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
		// If Legit is enabled, simply swim.
		if(this.legit.getValue()) {
			if(MC.player.isInLava() || MC.player.isTouchingWater()){
				MC.options.jumpKey.setPressed(true);
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
}
