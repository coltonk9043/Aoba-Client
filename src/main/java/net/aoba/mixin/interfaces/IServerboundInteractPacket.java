package net.aoba.mixin.interfaces;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerboundInteractPacket.class)
public interface IServerboundInteractPacket {
    @Invoker("write")
    void invokeWrite(FriendlyByteBuf buf);
}
