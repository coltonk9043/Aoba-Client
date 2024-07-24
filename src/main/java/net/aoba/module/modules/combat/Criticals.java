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
 * Criticals Module
 */
package net.aoba.module.modules.combat;

import net.aoba.module.Category;
import org.lwjgl.glfw.GLFW;
import io.netty.buffer.Unpooled;
import net.aoba.Aoba;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.mixin.interfaces.IPlayerInteractEntityC2SPacket;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module implements SendPacketListener {

	public enum InteractType {
        INTERACT, ATTACK, INTERACT_AT
    }
	
	private BooleanSetting legit;
	
	public Criticals() {
		super(new KeybindSetting("key.criticals", "Criticals Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

		this.setName("Criticals");
        this.setCategory(Category.of("Combat"));
		this.setDescription("Makes all attacks into critical strikes.");
		
		legit = new BooleanSetting("criticals_legit", "Legit", "Whether or not we will use the 'legit' mode.", false);
		
		this.addSetting(legit);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(SendPacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	@Override
	public void OnSendPacket(SendPacketEvent event) {
		Packet<?> packet = event.GetPacket();
		if(packet instanceof PlayerInteractEntityC2SPacket) {
			PlayerInteractEntityC2SPacket playerInteractPacket = (PlayerInteractEntityC2SPacket) packet;
			IPlayerInteractEntityC2SPacket packetAccessor = (IPlayerInteractEntityC2SPacket)playerInteractPacket;
			
			PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
			packetAccessor.invokeWrite(packetBuf);
			packetBuf.readVarInt();
			InteractType type = packetBuf.readEnumConstant(InteractType.class);
			
			if(type == InteractType.ATTACK) {
				MinecraftClient mc = MinecraftClient.getInstance();
				ClientPlayerEntity player = mc.player;
				if(player.isOnGround() && !player.isInLava() && !player.isSubmergedInWater()) {
					if(legit.getValue()) {
						player.jump();
					}else {
						ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
						networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.03125D, mc.player.getZ(), false));
						networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.0625D, mc.player.getZ(), false));
						networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
					}
				}
			}
		}
	}
}
