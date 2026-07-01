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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.aoba.Aoba;
import net.aoba.module.modules.combat.Nametags;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
	@Shadow
	public Font getFont() {
		return null;
	}

	@Inject(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V", at = @At("HEAD"))
	private void aoba$customNameTagStart(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera, int offset, CallbackInfo ci) {
		if (aoba$shouldCustomizeNameTag(state)) {
			float scale = (float) Aoba.getInstance().moduleManager.nametags.getNametagScale();
			Vec3 attach = state.nameTagAttachment;
			poseStack.pushPose();
			poseStack.translate(attach.x * (1.0 - scale), attach.y * (1.0 - scale), attach.z * (1.0 - scale));
			poseStack.scale(scale, scale, scale);
		}
	}

	@Inject(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V", at = @At("RETURN"))
	private void aoba$customNameTagEnd(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera, int offset, CallbackInfo ci) {
		if (aoba$shouldCustomizeNameTag(state)) {
			poseStack.popPose();
		}
	}

	@Unique
	private boolean aoba$shouldCustomizeNameTag(EntityRenderState state) {
		Nametags nametags = Aoba.getInstance().moduleManager.nametags;
		if (nametags == null || !nametags.state.getValue() || state.nameTagAttachment == null)
			return false;
		return !nametags.getPlayersOnly() || state instanceof AvatarRenderState;
	}
}
