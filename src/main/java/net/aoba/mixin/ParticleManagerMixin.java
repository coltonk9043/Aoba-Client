package net.aoba.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.event.events.ParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
	@Shadow
	@Nullable
	protected abstract <T extends ParticleEffect> Particle createParticle(T parameters, double x, double y, double z,
			double velocityX, double velocityY, double velocityZ);

	@Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
	private void onAddParticle(ParticleEffect parameters, double x, double y, double z, double velocityX,
			double velocityY, double velocityZ, CallbackInfoReturnable<Particle> info) {
		ParticleEvent event = new ParticleEvent(parameters);

		Aoba.getInstance().eventManager.Fire(event);

		if (event.isCancelled()) {
			if (parameters.getType() == ParticleTypes.FLASH)
				info.setReturnValue(createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ));
			else
				info.cancel();
		}
	}
}