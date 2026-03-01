/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.aoba.Aoba;
import net.aoba.module.modules.movement.Jesus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin extends Block implements BucketPickup {

	private LiquidBlockMixin(Properties blockSettings) {
		super(blockSettings);
	}

	@Inject(method = "getCollisionShape", at = @At(value = "HEAD"), cancellable = true)
	private void getCollisionShape(BlockState blockState_1, BlockGetter blockView_1, BlockPos blockPos_1,
			CollisionContext entityContext_1, CallbackInfoReturnable<VoxelShape> cir) {
		
		// If Aoba exists and Jesus is toggled (and NOT in legit mode)
		if (Aoba.getInstance() != null && Aoba.getInstance().moduleManager != null) {
			Jesus jesus = Aoba.getInstance().moduleManager.jesus;
			if (jesus != null && jesus.state.getValue() && !jesus.getLegit()) {
				cir.setReturnValue(Shapes.block());
				cir.cancel();
			}
		}
	}
}
