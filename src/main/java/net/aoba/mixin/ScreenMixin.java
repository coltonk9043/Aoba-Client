package net.aoba.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(at = {@At("HEAD")}, method = {"render(Lnet/minecraft/client/gui/DrawContext;IIF)V"}, cancellable = true)
    protected void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {

    }

    @Inject(at = {@At("HEAD")}, method = {"close()V"}, cancellable = true)
    protected void onClose(CallbackInfo ci) {

    }
}