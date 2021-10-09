package net.aoba.mixin;

import net.aoba.Aoba;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class IngameHudMixin {

	@Inject(at = {
	@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", ordinal = 4) }, 
	method = {"render(Lnet/minecraft/client/util/math/MatrixStack;F)V" })
	private void onRender(MatrixStack matrixStack, float partialTicks, CallbackInfo ci) {
		int scale = MinecraftClient.getInstance().options.guiScale;
		matrixStack.scale(1.0f/scale, 1.0f/scale, 1f);
		if (MinecraftClient.getInstance().options.debugEnabled)
			return;
		Aoba.getInstance().drawHUD(matrixStack, partialTicks);
		matrixStack.scale(scale, scale, 1f);
	}
}
