package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.utils.render.AobaRenderPipelines;
import net.minecraft.client.gl.ShaderLoader;

@Mixin(ShaderLoader.class)
public abstract class ShaderLoaderMixin {
	@Inject(method = "apply(Lnet/minecraft/client/gl/ShaderLoader$Definitions;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("TAIL"))
	private void reloadPipelines(CallbackInfo info) {
		AobaRenderPipelines.precompile();
	}
}