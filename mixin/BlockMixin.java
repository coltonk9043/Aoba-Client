package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.aoba.Aoba;
import net.aoba.module.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Mixin(Block.class)
public abstract class BlockMixin implements ItemConvertible {

	@Inject(at = { @At("HEAD") }, method = {
			"shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z" }, cancellable = true)
	private static void onShouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction direction,
			BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		if (Aoba.getInstance().mm.xray.getState())
			cir.setReturnValue(XRay.isXRayBlock(state.getBlock()));
	}

	@Inject(at = { @At("HEAD") }, method = { "getVelocityMultiplier()F" }, cancellable = true)
	private void onGetVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
		if(!Aoba.getInstance().mm.noslowdown.getState()) return;
		if (cir.getReturnValueF() < 1.0f)
			cir.setReturnValue(1F);
	}

}
