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
 * Anti-Invis Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;

import net.aoba.core.settings.types.KeybindSetting;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;

public class AntiInvis extends Module {
	
	public AntiInvis() {
		super(new KeybindSetting("key.antiinvis", "AntiInvis Key", new KeyBinding("key.antiinvis", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba")));
		this.setName("AntiInvis");
		this.setCategory(Category.Combat);
		this.setDescription("Reveals players who are invisible.");
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