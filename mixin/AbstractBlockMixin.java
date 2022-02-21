package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.aoba.Aoba;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin implements ItemConvertible {

	@Inject(at = { @At("HEAD") }, method = {
			"getAmbientOcclusionLightLevel(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F" }, cancellable = true)
	public void onGetAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos,
			CallbackInfoReturnable<Float> cir) {
		if (!Aoba.getInstance().mm.xray.getState())
			return;
		cir.setReturnValue(1F);
	}
}
