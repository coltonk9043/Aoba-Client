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
 * PlayerESP Module
 */
package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.RenderEvent;
import net.aoba.event.listeners.RenderListener;
import net.aoba.gui.colors.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.InputUtil;

public class PlayerESP extends Module implements RenderListener {
	
	private ColorSetting color_default = new ColorSetting("playeresp_color_default", "Default Color",  "Default Color", new Color(1f, 1f, 0f));
	private ColorSetting color_friendly = new ColorSetting("playeresp_color_friendly", "Friendly Color",  "Friendly Color", new Color(0f, 1f, 0f));
	private ColorSetting color_enemy = new ColorSetting("playeresp_color_enemy", "Enemy Color", "Enemy Color", new Color(1f, 0f, 0f));
	
	public PlayerESP() {
		super(new KeybindSetting("key.playeresp", "PlayerESP Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("PlayerESP");
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see other players with an ESP.");
		
		this.addSetting(color_default);
		this.addSetting(color_friendly);
		this.addSetting(color_enemy);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(RenderListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(RenderListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	@Override
	public void OnRender(RenderEvent event) {
		for (AbstractClientPlayerEntity entity : MC.world.getPlayers()) {
			if(entity != MC.player) {
				RenderUtils.draw3DBox(event.GetMatrixStack(), entity.getBoundingBox(), color_default.getValue());
			}
		}
	}
}
