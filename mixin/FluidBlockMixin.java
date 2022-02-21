package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
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

	private FluidBlockMixin(Settings blockSettings) {
		super(blockSettings);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1,
			ShapeContext entityContext_1) {
		if (Aoba.getInstance().mm.jesus.getState()) {
			return VoxelShapes.fullCube();
		}
		return super.getCollisionShape(blockState_1, blockView_1, blockPos_1,entityContext_1);
	}
}
