package net.aoba.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Inject(at = {@At("HEAD")}, method = "setHealth(F)V")
    public void onSetHealth(float health, CallbackInfo ci) {
        return;
    }

    @Inject(at = {@At("HEAD")}, method = "tickNewAi()V", cancellable = true)
    public void onTickNewAi(CallbackInfo ci) {

    }
}
