package net.aoba.mixin.interfaces;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

@Mixin(BossBarS2CPacket.class)
public interface IBossBarS2CPacket {
	@Accessor("uuid")
	UUID getUuid();

	// TODO: Need to use access widener for this...
	@Accessor("action")
	BossBarS2CPacket.Action getAction();
}
