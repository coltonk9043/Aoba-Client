package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
	@Inject (at = {@At("HEAD")}, method="setHealth(F)V")
	public void onSetHealth(float health, CallbackInfo ci) {
		return;
	}
	
	@Inject (at = {@At("HEAD")}, method="tickNewAi()V", cancellable = true)
	public void onTickNewAi(CallbackInfo ci) {

	}
}
