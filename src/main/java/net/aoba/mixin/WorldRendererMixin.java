package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Inject(at = { @At("RETURN") }, method = { "render" })
	private void onRenderWorld(MatrixStack matrixStack, float tickDelta, long limitTime, boolean renderBlockOutline,
			Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
			CallbackInfo info) {
		Aoba.getInstance().moduleManager.render(matrixStack);
	}

	@Inject(at = @At("HEAD"), method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z", cancellable = true)
	private void onHasBlindnessOrDarknessEffect(Camera camera, CallbackInfoReturnable<Boolean> cir) {
		if (Aoba.getInstance().moduleManager.nooverlay.getState())
			cir.setReturnValue(false);
	}
}
