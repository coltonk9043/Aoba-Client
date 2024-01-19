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

import org.lwjgl.glfw.GLFW;

import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;

public class FastBreak extends Module implements TickListener {

	private FloatSetting multiplier;
	
	public FastBreak() {
		super(new KeybindSetting("key.fastbreak", "FastBreak Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("FastBreak");
		this.setCategory(Category.Misc);
		this.setDescription("Breaks blocks quicker based on a multiplier.");
		
		multiplier = new FloatSetting("fastbreak_multiplier", "Multiplier", "Multiplier for how fast the blocks will break.", 1.25f, 1f, 3f, 0.05f);
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
	public void OnUpdate(TickEvent event) {
	}
}
