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
 * Reach Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.core.settings.types.KeybindSetting;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;

public class Reach extends Module {
	
	private FloatSetting distance;
	
	public Reach() {
		super(new KeybindSetting("key.reach", "Reach Key", new KeyBinding("key.reach", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba")));
		
		this.setName("Reach");
		this.setCategory(Category.Combat);
		this.setDescription("Reaches further.");
		
		distance = new FloatSetting("reach_distance", "Distance", "Distance, in blocks, that you can reach.", 5f, 1f, 15f, 1f);
		this.addSetting(distance);
	}

	public float getReach() {
		return distance.getValue().floatValue();
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
	
	public void setReachLength(float reach) {
		this.distance.setValue((double)reach);
	}
}