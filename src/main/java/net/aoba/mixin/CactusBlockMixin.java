package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin extends Block {

	public CactusBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = { @At("HEAD") }, method = {
			"getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;" }, cancellable = true)
	private void onGetCollisionShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1,
			ShapeContext entityContext_1, CallbackInfoReturnable<VoxelShape> cir) {
		if(Aoba.getInstance() != null) {
			if(Aoba.getInstance().moduleManager.anticactus.getState()) {
				cir.setReturnValue(VoxelShapes.fullCube());
			}
		}
	}
}
