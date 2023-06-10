package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import net.aoba.Aoba;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Inject(at = { @At("HEAD") }, method = { "sendPacket(Lnet/minecraft/network/packet/Packet;)V" }, cancellable = true)
	private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
		Aoba.getInstance().mm.sendPacket(packet);
	}
}
