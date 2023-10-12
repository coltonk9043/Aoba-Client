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
 * Sneak Module
 */
package net.aoba.module.modules.movement;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.core.settings.types.KeybindSetting;
import net.aoba.event.events.TickEvent;
import net.aoba.event.listeners.TickListener;
import net.aoba.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class Sneak extends Module implements TickListener {

	public Sneak() {
		super(new KeybindSetting("key.sneakhack", "Sneak Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("Sneak");
		this.setCategory(Category.Movement);
		this.setDescription("Makes the player appear like they're sneaking.");
	}

	@Override
	public void onDisable() {
		ClientPlayerEntity player = MC.player;
		player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.RELEASE_SHIFT_KEY));
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
		
		MC.player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.PRESS_SHIFT_KEY));
		MC.player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, Mode.RELEASE_SHIFT_KEY));
	}
}

