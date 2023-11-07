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

import org.lwjgl.glfw.GLFW;

import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Timer extends Module {
	private FloatSetting multiplier;
	
	public Timer() {
		super(new KeybindSetting("key.timer", "Timer Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("Timer");
		this.setCategory(Category.Misc);
		this.setDescription("Increases the speed of Minecraft.");
		
		this.multiplier = new FloatSetting("timer_multiplier", "Multiplier", "The multiplier that will affect the game speed.", 1f, 0.1f, 15f, 0.1f);
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
}