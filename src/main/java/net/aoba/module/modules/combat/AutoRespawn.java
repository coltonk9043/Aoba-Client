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
 * AutoRespawn Module
 */
package net.aoba.module.modules.combat;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.core.settings.types.KeybindSetting;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.module.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

public class AutoRespawn extends Module implements ReceivePacketListener {
	
	public AutoRespawn() {
		
		super(new KeybindSetting("key.autorespawn", "AutoRespawn Key", new KeyBinding("key.autorespawn", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba")));

		this.setName("AutoRespawn");

		this.setCategory(Category.Combat);
		this.setDescription("Automatically respawns when you die.");
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	@Override
	public void OnReceivePacket(ReceivePacketEvent readPacketEvent) {
		Packet<?> packet = readPacketEvent.GetPacket();
		if(packet instanceof HealthUpdateS2CPacket) {
			HealthUpdateS2CPacket healthPacket = (HealthUpdateS2CPacket)packet;
			if (healthPacket.getHealth() > 0.0F)
				return;
			MC.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
		}
	}
}
