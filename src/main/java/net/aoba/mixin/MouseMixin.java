package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.aoba.event.events.MouseMoveEvent;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin
{
	@Inject(at = {@At("HEAD")}, method = {"lockCursor()V"}, cancellable = true)
	private void onLockCursor(CallbackInfo ci)
	{
		if(Aoba.getInstance().hudManager.isClickGuiOpen())	
			ci.cancel();
	}
	
	@Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;updateMouse()V") }, method = {"onCursorPos(JDD)V" }, cancellable = true)
	private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
		if(Aoba.getInstance().hudManager.isClickGuiOpen()) {
			MouseMoveEvent event = new MouseMoveEvent(x, y);
			Aoba.getInstance().eventManager.Fire(event);
			
			if(event.IsCancelled())
				ci.cancel();
		}
	}
}