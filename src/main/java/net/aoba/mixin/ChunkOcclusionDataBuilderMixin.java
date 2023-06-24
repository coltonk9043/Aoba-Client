package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.aoba.Aoba;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;

@Mixin(ChunkOcclusionDataBuilder.class)
public class ChunkOcclusionDataBuilderMixin {
	@Inject(at = {@At("HEAD")},
			method = {"markClosed(Lnet/minecraft/util/math/BlockPos;)V"},
			cancellable = true)
		private void onMarkClosed(BlockPos pos, CallbackInfo ci)
		{
			if(Aoba.getInstance().moduleManager.xray.getState()) {
				ci.cancel();
			}
		}
}
