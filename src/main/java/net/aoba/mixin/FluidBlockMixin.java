package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import net.aoba.Aoba;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin extends Block implements FluidDrainable {

	private  FluidBlockMixin(Settings blockSettings) {
		super(blockSettings);
	}

	@Inject(method = "getCollisionShape", at = @At(value = "HEAD"), cancellable = true)
	private void getCollisionShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1,
			ShapeContext entityContext_1, CallbackInfoReturnable<VoxelShape> cir) {
		if (Aoba.getInstance().mm.jesus.getState()) {
			cir.setReturnValue(VoxelShapes.fullCube());
			cir.cancel();
		}
	}
}
