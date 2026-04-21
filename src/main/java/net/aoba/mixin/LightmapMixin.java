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
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(Lightmap.class)
public class LightmapMixin {
	@Inject(at = { @At("HEAD") }, method = {
			"getBrightness(Lnet/minecraft/world/level/dimension/DimensionType;I)F" }, cancellable = true)
	private static void onGetBrightness(DimensionType dimensionType, int level,
			CallbackInfoReturnable<Float> cir) {
		if (Aoba.getInstance().moduleManager.norender.state.getValue()) {
			cir.setReturnValue(0F);
		}
	}
}
