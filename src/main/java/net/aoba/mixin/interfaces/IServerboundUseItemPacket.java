package net.aoba.mixin.interfaces;

import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundUseItemPacket.class)
public interface IServerboundUseItemPacket {
	@Mutable
	@Accessor("yRot")
	void setYRot(float yaw);

	@Mutable
	@Accessor("xRot")
	void setXRot(float pitch);
}
