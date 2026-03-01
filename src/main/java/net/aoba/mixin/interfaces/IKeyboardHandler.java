package net.aoba.mixin.interfaces;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KeyboardHandler.class)
public interface IKeyboardHandler {
	@Invoker("keyPress")
	void invokeKeyPress(long window, int action, KeyEvent keyEvent);
}
