package net.aoba.utils.rotation;

import net.aoba.Aoba;
import net.aoba.event.events.SendPacketEvent;
import net.aoba.event.listeners.SendPacketListener;
import net.aoba.mixin.interfaces.IPlayerMoveC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class RotationManager implements SendPacketListener {
	private static MinecraftClient MC = MinecraftClient.getInstance();

	public static Rotation serverRotation = new Rotation(0, 0);

	public RotationManager() {
		Aoba.getInstance().eventManager.AddListener(SendPacketListener.class, this);
	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		Packet<?> packet = event.GetPacket();
		if (packet instanceof PlayerMoveC2SPacket) {
			PlayerMoveC2SPacket playerMovePacket = (PlayerMoveC2SPacket) packet;
			IPlayerMoveC2SPacket iPlayerMovePacket = (IPlayerMoveC2SPacket) packet;
			if (!playerMovePacket.changesLook())
				return;

			serverRotation = new Rotation(iPlayerMovePacket.getYaw(), iPlayerMovePacket.getPitch());
		} else if (packet instanceof PlayerPositionLookS2CPacket) {
			PlayerPositionLookS2CPacket playerPositionLookPacket = (PlayerPositionLookS2CPacket) packet;
			serverRotation = new Rotation(playerPositionLookPacket.getYaw(), playerPositionLookPacket.getPitch());
		} else if (packet instanceof PlayerInteractItemC2SPacket) {
			PlayerInteractItemC2SPacket playerInteractItemC2SPacket = (PlayerInteractItemC2SPacket) packet;
			serverRotation = new Rotation(playerInteractItemC2SPacket.getYaw(), playerInteractItemC2SPacket.getPitch());
		}
	}

	public static double getGCD() {
		double f = MC.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
		return f * f * f * 1.2;
	}
}
