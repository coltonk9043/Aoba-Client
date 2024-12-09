package net.aoba.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class SodiumBlockOcclusionCacheMixin {

	@Inject(at = { @At("RETURN") }, method = "shouldDrawSide", cancellable = true)
	private void onShouldDrawSide(BlockState state, BlockView view, BlockPos pos, Direction facing,
								  CallbackInfoReturnable<Boolean> cir) {
		AobaClient aoba = Aoba.getInstance();
		XRay xray = aoba.moduleManager.xray;
		if (xray.state.getValue()) {
			cir.setReturnValue(xray.isXRayBlock(state.getBlock()));
		}
	}
}