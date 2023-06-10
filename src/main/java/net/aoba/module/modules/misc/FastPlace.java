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
 * FastPlace Module
 */
package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.interfaces.IMinecraftClient;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;

public class FastPlace extends Module {
	IMinecraftClient iMC;
	
	public FastPlace() {
		this.setName("FastPlace");
		this.setBind(new KeyBinding("key.fastplace", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Places blocks exceptionally fast");
	}

	@Override
	public void onDisable() {
		IMC.setItemUseCooldown(4);
	}

	@Override
	public void onEnable() {
		
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
		IMC.setItemUseCooldown(0);
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