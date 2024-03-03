/*
* Aoba Hacked Client
* Copyright (C) 2019-2024 coltonk9043
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
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;

public class FastBreak extends Module {

	public FloatSetting multiplier;
	public BooleanSetting ignoreWater;
	
	public FastBreak() {
		super(new KeybindSetting("key.fastbreak", "FastBreak Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("FastBreak");
		this.setCategory(Category.Misc);
		this.setDescription("Breaks blocks quicker based on a multiplier.");
		
		multiplier = new FloatSetting("fastbreak_multiplier", "Multiplier", "Multiplier for how fast the blocks will break.", 1.25f, 1f, 10f, 0.05f);
		ignoreWater = new BooleanSetting("fastbreak_ignore_water", "Ignore Water", "Ignores the slowdown that being in water causes.", false);
		
		this.addSetting(multiplier);
		this.addSetting(ignoreWater);
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
}
