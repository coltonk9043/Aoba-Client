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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.aoba.Aoba;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(at = {@At("HEAD")}, method = { "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"}, cancellable = true)
	private void onBobViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (Aoba.getInstance().moduleManager.nooverlay.getState()) {
			ci.cancel();
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0), method = {
			"renderWorld(FJ)V" })
	private float nauseaLerp(float delta, float first, float second) {
		if (Aoba.getInstance().moduleManager.nooverlay.getState())
			return 0;
		
		return MathHelper.lerp(delta, first, second);
	}
}
