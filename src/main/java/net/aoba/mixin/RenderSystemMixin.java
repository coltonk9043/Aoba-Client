package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.aoba.utils.render.mesh.MeshRenderer;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
	@Inject(method = "flipFrame", at = @At("TAIL"))
	private static void onFlipFrame(CallbackInfo info) {
		MeshRenderer.flipFrame();
	}
}