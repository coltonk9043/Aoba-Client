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
 * FastBreak Module
 */
package net.aoba.module.modules.misc;

import net.aoba.core.osettings.osettingtypes.DoubleOSetting;
import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;

public class FastBreak extends Module {

	private DoubleOSetting multiplier;
	
	public FastBreak() {
		this.setName("FastBreak");
		this.setBind(new KeyBinding("key.fastbreak", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Breaks blocks quicker based on a multiplier.");
		
		multiplier = new DoubleOSetting("fastbreak_multiplier", "Multiplier", 1.25f, null, 1f, 3f, 0.05f);
		this.addSetting(multiplier);
	}

	public void setMultiplier(float multiplier) { this.multiplier.setValue((double)multiplier); }
	
	public float getMultiplier() {
		return this.multiplier.getValue().floatValue();
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
	}

	@Override
	public void onReceivePacket(Packet<?> packet) {
	}
}
