package net.aoba.mixin.interfaces;

import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundBossEventPacket.class)
public interface IClientboundBossEventPacket {
	@Accessor("id")
	UUID getUuid();

	// TODO: Need to use access widener for this...
	@Accessor("operation")
	ClientboundBossEventPacket.Operation getAction();
}
