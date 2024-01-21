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
 * AutoSign Module
 */
package net.aoba.module.modules.world;

import org.lwjgl.glfw.GLFW;
import net.aoba.cmd.CommandManager;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;

public class AutoSign extends Module {
	String[] text;

	public AutoSign() {
		super(new KeybindSetting("key.autosign", "AutoSign Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("AutoSign");
		this.setCategory(Category.World);
		this.setDescription("Automatically places sign.");
	}

	public void setText(String[] text) {
		this.text = text;
	}
	
	public String[] getText() {
		return this.text;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		CommandManager.sendChatMessage("Place down a sign to set text!");
		this.text = null;
	}

	@Override
	public void onToggle() {
	}
}
