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
 * AutoFish Module
 */
package net.aoba.module.modules.misc;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.module.Module;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class AutoFish extends Module implements ReceivePacketListener {
	public AutoFish() {
		super(new KeybindSetting("key.autofish", "AutoFish Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("AutoFish");
		this.setCategory(Category.Misc);
		this.setDescription("Automatically fishes for you.");
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
	
	private void recastRod() {
		
		PlayerInteractItemC2SPacket packetTryUse = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0);
		MC.player.networkHandler.sendPacket(packetTryUse);
		MC.player.networkHandler.sendPacket(packetTryUse);
	}

	@Override
	public void OnReceivePacket(ReceivePacketEvent readPacketEvent) {
		Packet<?> packet = readPacketEvent.GetPacket();
		
		if(packet instanceof PlaySoundS2CPacket ) {
			PlaySoundS2CPacket soundPacket = (PlaySoundS2CPacket)packet;
			if(soundPacket.getSound().value().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH)) {
				recastRod();
			}
		}
	}

}
