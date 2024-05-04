package net.aoba.mixin;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.network.AbstractClientPlayerEntity;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntityMixin {

	@Inject(method = "isSpectator()Z", at = @At("HEAD"), cancellable = true)
	public void onIsSpectator(CallbackInfoReturnable<Boolean> cir) {
		return;
	}
}
