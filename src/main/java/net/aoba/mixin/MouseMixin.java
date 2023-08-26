package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin
{
	@Inject(at = {@At("HEAD")}, method = {"lockCursor()V"}, cancellable = true)
	private void onLockCursor(CallbackInfo ci)
	{
		if(Aoba.getInstance().hudManager.isClickGuiOpen()) 	ci.cancel();
	}
}