package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.systems.RenderSystem;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
//	@Inject(method = "flipFrame", at = @At("TAIL"))
//	private static void onFlipFrame(CallbackInfo info) {
//		MeshRenderer.flipFrame();
//	}
}