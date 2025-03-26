/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.combat;

import io.netty.buffer.Unpooled;
import net.aoba.Aoba;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.mixin.interfaces.IPlayerInteractEntityC2SPacket;
import net.aoba.module.AntiCheat;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module implements SendPacketListener {

	public enum InteractType {
		INTERACT, ATTACK, INTERACT_AT
	}

	private final BooleanSetting legit = BooleanSetting.builder().id("criticals_legit").displayName("Legit")
			.description("Whether or not we will use the 'legit' mode.").defaultValue(false).build();

	public Criticals() {
		super("Criticals");

		setCategory(Category.of("Combat"));
		setDescription("Makes all attacks into critical strikes.");

		addSetting(legit);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Karhu
		);
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
	public void onSendPacket(SendPacketEvent event) {
		Packet<?> packet = event.GetPacket();
		if (packet instanceof PlayerInteractEntityC2SPacket playerInteractPacket) {
            IPlayerInteractEntityC2SPacket packetAccessor = (IPlayerInteractEntityC2SPacket) playerInteractPacket;

			PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
			packetAccessor.invokeWrite(packetBuf);
			packetBuf.readVarInt();
			InteractType type = packetBuf.readEnumConstant(InteractType.class);

			if (type == InteractType.ATTACK) {
				MinecraftClient mc = MinecraftClient.getInstance();
				ClientPlayerEntity player = mc.player;
				if (player.isOnGround() && !player.isInLava() && !player.isSubmergedInWater()) {
					if (legit.getValue()) {
						player.jump();
					} else {
						ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
						networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(),
								mc.player.getY() + 0.03125D, mc.player.getZ(), false, false));
						networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(),
								mc.player.getY() + 0.0625D, mc.player.getZ(), false, false));
						networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(),
								mc.player.getY(), mc.player.getZ(), false, false));
					}
				}
			}
		}
	}
}
