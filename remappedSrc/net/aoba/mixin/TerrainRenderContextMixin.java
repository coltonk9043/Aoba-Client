package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.aoba.module.modules.render.XRay;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Mixin(TerrainRenderContext.class)
public class TerrainRenderContextMixin {
	@Inject(at = { @At("HEAD") }, method = { "tessellateBlock" }, cancellable = true, remap = false)
	private void tesselateBlock(BlockState blockState, BlockPos blockPos, final BakedModel model,
			MatrixStack matrixStack, CallbackInfo ci) {
		if (Aoba.getInstance().mm.xray.getState()) {
			if (XRay.isXRayBlock(blockState.getBlock())) {
				ci.cancel();
				return;
			}
		}
	}
}
