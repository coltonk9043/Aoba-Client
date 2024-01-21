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
 * Glide Module
 */
package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;

public class Glide extends Module implements TickListener {
	private float fallSpeed = .25f;
	public Glide() {
		super(new KeybindSetting("key.glide", "Glide Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("Glide");
		this.setCategory(Category.Movement);
		this.setDescription("Allows the player to glide down.");
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void OnUpdate(TickEvent event) {
		ClientPlayerEntity player = MC.player;
		if(player.getVelocity().y < 0 && (!player.isOnGround() || !player.isInLava() || !player.isSubmergedInWater() || !player.isHoldingOntoLadder())) {
			player.setVelocity(player.getVelocity().x, Math.max(player.getVelocity().y, -this.fallSpeed), player.getVelocity().z);
		}
	}
}
