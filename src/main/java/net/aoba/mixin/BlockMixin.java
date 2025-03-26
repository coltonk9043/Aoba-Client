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
import net.aoba.AobaClient;
import net.aoba.module.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.Direction;

@Mixin(Block.class)
public abstract class BlockMixin implements ItemConvertible {

	@Inject(at = { @At("RETURN") }, method = {
			"shouldDrawSide" }, cancellable = true)
	private static void onShouldDrawSide(BlockState state, BlockState otherState, Direction side,
										 CallbackInfoReturnable<Boolean> cir) {
		AobaClient aoba = Aoba.getInstance();
		XRay xray = aoba.moduleManager.xray;
		if (xray.state.getValue()) {
			cir.setReturnValue(xray.isXRayBlock(state.getBlock()));
		}
	}

	@Inject(at = { @At("HEAD") }, method = { "getVelocityMultiplier()F" }, cancellable = true)
	private void onGetVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
		if (!Aoba.getInstance().moduleManager.noslowdown.state.getValue())
			return;
		if (cir.getReturnValueF() < 1.0f)
			cir.setReturnValue(1F);
	}

}
