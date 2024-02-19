package net.aoba.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.events.LeftMouseDownEvent;
import net.aoba.event.events.LeftMouseUpEvent;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin
{
	@Shadow
	private double x;
	@Shadow
	private double y;
	
	@Inject(at = {@At("HEAD")}, method = {"onMouseButton(JIII)V"}, cancellable = true)
	private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		switch(button) {
		case GLFW.GLFW_MOUSE_BUTTON_LEFT:
			if(action==1) {
				LeftMouseDownEvent event = new LeftMouseDownEvent(x, y);
				Aoba.getInstance().eventManager.Fire(event);
				
				if(event.IsCancelled()) {
					ci.cancel();
				}
			}else {
				LeftMouseUpEvent event = new LeftMouseUpEvent(x, y);
				Aoba.getInstance().eventManager.Fire(event);
				
				if(event.IsCancelled()) {
					ci.cancel();
				}
			}
			break;
		case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
			
			break;
		}
	}
	
	@Inject(at = {@At("HEAD")}, method= {"onMouseScroll(JDD)V"}, cancellable = true)
	private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		MouseScrollEvent event = new MouseScrollEvent(horizontal, vertical);
		Aoba.getInstance().eventManager.Fire(event);
		
		if(event.IsCancelled()) {
			ci.cancel();
		}
	}
	
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