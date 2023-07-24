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
 * Timer Module
 */
package net.aoba.module.modules.misc;

import net.aoba.core.osettings.osettingtypes.DoubleOSetting;
import org.lwjgl.glfw.GLFW;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
public class Timer extends Module {
	private DoubleOSetting multiplier;
	
	public Timer() {
		this.setName("Timer");
		this.setBind(new KeyBinding("key.timer", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Misc);
		this.setDescription("Increases the speed of Minecraft.");
		this.multiplier = new DoubleOSetting("timer_multiplier", "Multiplier", 1f, null, 0.1f, 15f, 0.1f);
		this.addSetting(multiplier);
	}

	public float getMultiplier() {
		return this.multiplier.getValue().floatValue();
	}
	
	public void setMultipler(float multiplier) {
		this.multiplier.setValue((double)multiplier);
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