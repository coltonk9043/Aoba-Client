package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.event.events.PlaySoundEvent;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
	private void onPlay(SoundInstance soundInstance, CallbackInfo info) {
		if (Aoba.getInstance().eventManager != null) {
			PlaySoundEvent event = new PlaySoundEvent(soundInstance);
			Aoba.getInstance().eventManager.Fire(event);

			if (event.isCancelled())
				info.cancel();
		}
	}
}