package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.event.events.KeyDownEvent;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	
	@Inject(at = {@At("HEAD")}, method = {"onKey(JIIII)V" }, cancellable = true)
	private void OnKeyDown(long window, int key, int scancode,
			int action, int modifiers, CallbackInfo ci) {
		KeyDownEvent event = new KeyDownEvent(window, key, scancode, action, modifiers);
		Aoba.getInstance().eventManager.Fire(event);
		
		if(event.IsCancelled()) {
			ci.cancel();
		}
	}
}
