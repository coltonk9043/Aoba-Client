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
 * Nametags Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.core.settings.types.BooleanSetting;
import net.aoba.core.settings.types.FloatSetting;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;

public class Nametags extends Module {

	private FloatSetting scale;
	private BooleanSetting onlyPlayers;
	private BooleanSetting alwaysVisible;
	
	public Nametags() {
		this.setName("Nametags");
		this.setBind(new KeyBinding("key.nametags", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Combat);
		this.setDescription("Scales the nametags to be larger.");
		
		scale = new FloatSetting("nametags_scale", "Scale of the NameTags", 0, 0, 5, 0.25);
		onlyPlayers = new BooleanSetting("nametags_onlyPlayers", "Whether Nametags are only enlarged for players.", false, null);
		alwaysVisible = new BooleanSetting("nametags_alwaysVisible", "Whether Nametags will always be displayed.", false, null);
		this.addSetting(scale);
		this.addSetting(onlyPlayers);
		this.addSetting(alwaysVisible);
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
	
	public double getNametagScale() {
		return this.scale.getValue();
	}
	
	public boolean getPlayersOnly() {
		return this.onlyPlayers.getValue();
	}
	
	public boolean getAlwaysVisible() {
		return this.alwaysVisible.getValue();
	}
}