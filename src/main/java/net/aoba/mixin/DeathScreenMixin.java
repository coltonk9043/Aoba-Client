package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.aoba.gui.GuiManager;
import net.minecraft.client.gui.screen.DeathScreen;


@Mixin(DeathScreen.class)
public class DeathScreenMixin{
	
	@Inject(at = { @At("HEAD") }, method = "init()V", cancellable = true)
	private void onInit(CallbackInfo ci) {
		GuiManager hudManager = Aoba.getInstance().hudManager;
		if(hudManager.isClickGuiOpen()) {
			hudManager.setClickGuiOpen(false);
		}
	}
}
