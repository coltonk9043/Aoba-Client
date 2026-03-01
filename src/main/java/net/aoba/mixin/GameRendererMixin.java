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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.render.NoRender;
import net.aoba.utils.render.Render2D;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	private Camera mainCamera;

	@Shadow
	private Minecraft minecraft;

	@Inject(at = { @At("HEAD") }, method = {
			"bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V" }, cancellable = true)
	private void onBobViewWhenHurt(PoseStack matrixStack, float f, CallbackInfo ci) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba != null && aoba.moduleManager.norender.state.getValue()) {
			ci.cancel();
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0), method = {
			"renderLevel(Lnet/minecraft/client/DeltaTracker;)V" })
	private float nauseaLerp(float delta, float first, float second) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba != null && aoba.moduleManager.norender.state.getValue())
			return 0;

		return Mth.lerp(delta, first, second);
	}

	@Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = {
			"ldc=hand" }))
	private void onRenderWorld(DeltaTracker tickCounter, CallbackInfo ci) {
		if (minecraft == null || minecraft.level == null || minecraft.player == null) {
			return;
		}

		Render2D.updateScreenCenter();
	}

	@Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true)
	private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo info) {
		AobaClient aoba = Aoba.getInstance();
		if (aoba == null)
			return;

		NoRender norender = aoba.moduleManager.norender;
		if (floatingItem.getItem() == Items.TOTEM_OF_UNDYING && norender.state.getValue()
				&& norender.getNoTotemAnimation()) {
			info.cancel();
		}
	}
}
