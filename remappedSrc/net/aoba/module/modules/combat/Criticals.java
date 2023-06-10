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
 * Criticals Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;

public class Criticals extends Module {

	public Criticals() {
		this.setName("Criticals");
		this.setBind(new KeyBinding("key.criticals", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Makes all attacks into critical strikes.");
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

	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {

	}

	@Override
	public void onSendPacket(Packet<?> packet) {
//		if (packet instanceof UseEntityC2SPacket) {
//			CUseEntityPacket packetUseEntity = (CUseEntityPacket) packet;
//			if (packetUseEntity.getAction() == CUseEntityPacket.Action.ATTACK) {
//				if(mc.player.isOnGround()) {
//					boolean preGround = mc.player.isOnGround();
//					mc.player.setOnGround(false);
//					mc.player.jump();
//					mc.player.setOnGround(preGround);
//				}
//				
//			}
//		}
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
		
		
	}
}
