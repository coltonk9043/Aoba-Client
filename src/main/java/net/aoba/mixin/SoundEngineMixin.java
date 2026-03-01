package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.event.events.PlaySoundEvent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
	@Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
	private void onPlay(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
		if (Aoba.getInstance().eventManager != null) {
			PlaySoundEvent event = new PlaySoundEvent(soundInstance);
			Aoba.getInstance().eventManager.Fire(event);

			if (event.isCancelled())
				cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
		}
	}
}