package net.aoba.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(at = {@At("HEAD")}, method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"}, cancellable = true)
    protected void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

    }

    @Inject(at = {@At("HEAD")}, method = {"onClose()V"}, cancellable = true)
    protected void onClose(CallbackInfo ci) {

    }
}