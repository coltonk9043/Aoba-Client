package net.aoba.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntityMixin {

    @Inject(method = "isSpectator()Z", at = @At("HEAD"), cancellable = true)
    public void onIsSpectator(CallbackInfoReturnable<Boolean> cir) {
        return;
    }
}
