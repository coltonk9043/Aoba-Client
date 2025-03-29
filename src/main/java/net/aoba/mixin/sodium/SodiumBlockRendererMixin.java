package net.aoba.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;

@Mixin(value = BlockRenderer.class)
public abstract class SodiumBlockRendererMixin {
	// @Inject(method = "renderModel", at = @At(value = "INVOKE", target =
	// "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProviderRegistry;getColorProvider(Lnet/minecraft/block/Block;)Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;",
	// shift = At.Shift.AFTER), cancellable = true)
	// private void onRenderModel(BakedModel model, BlockState state, BlockPos pos,
	// BlockPos origin, CallbackInfo ci) {
	// AobaClient aoba = Aoba.getInstance();
	// XRay xray = aoba.moduleManager.xray;
	// if (xray.state.getValue()) {
	// if (!xray.isXRayBlock(state.getBlock())) {
	// ci.cancel();
	// }
	// }
	// }
}