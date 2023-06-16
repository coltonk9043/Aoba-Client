package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import net.aoba.Aoba;
import net.aoba.module.modules.movement.Jesus;
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
		// If Aoba exists and Jesus is toggled (and NOT in legit mode)
		if(Aoba.getInstance() != null) {
			Jesus jesus = (Jesus) Aoba.getInstance().moduleManager.jesus;
			if (jesus.getState() && !jesus.legit.getValue()) {
				cir.setReturnValue(VoxelShapes.fullCube());
				cir.cancel();
			}
		}
	}
}
