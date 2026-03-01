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
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
	@Shadow
	@Nullable
	protected abstract <T extends ParticleOptions> Particle makeParticle(T parameters, double x, double y, double z,
			double velocityX, double velocityY, double velocityZ);

	@Inject(method = "createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
	private void onAddParticle(ParticleOptions parameters, double x, double y, double z, double velocityX,
			double velocityY, double velocityZ, CallbackInfoReturnable<Particle> info) {
		ParticleEvent event = new ParticleEvent(parameters);

		Aoba.getInstance().eventManager.Fire(event);

		if (event.isCancelled()) {
			if (parameters.getType() == ParticleTypes.FLASH)
				info.setReturnValue(makeParticle(parameters, x, y, z, velocityX, velocityY, velocityZ));
			else
				info.cancel();
		}
	}
}