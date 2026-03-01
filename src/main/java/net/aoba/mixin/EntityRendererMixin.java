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

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.module.modules.combat.Nametags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
	// TODO: Add an option to toggle custom nametag rendering in the future in case
	// users would like a noncustom name tag.
	/*
	 * @Inject(at = @At(value = "HEAD"), method =
	 * "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	 * cancellable = true) protected void onRenderLabelIfPresent(T state, Text text,
	 * MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
	 * CallbackInfo ci) { // CustomRenderLabel(entity, text, matrices,
	 * vertexConsumers, light); // ci.cancel(); }
	 */

	@Shadow
	public Font getFont() {
		return null;
	}

	/**
	 * Custom Label Render that will allow us to Render what we'd like in the
	 * future.
	 *
	 * @param entity          Entity being currently rendered.
	 * @param text            The text to render.
	 * @param matrices        The MatrixStack.
	 * @param vertexConsumers Vertex Consumers
	 * @param light           Light level.
	 */
	protected void CustomRenderLabel(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers,
			int light) {
		Minecraft mc = Minecraft.getInstance();
		AobaClient aoba = Aoba.getInstance();

		double d = mc.player != null ? mc.player.distanceToSqr(entity) : 0;
		if (d > 4096.0) {
			return;
		}
		boolean bl = !entity.isDiscrete();
		// TODO: Get name line height
		int i = "deadmau5".equals(text.getString()) ? -10 : 0;
		matrices.pushPose();
		matrices.translate(0.0f, 1.0f, 0.0f);
		matrices.mulPose(mc.gameRenderer.getMainCamera().rotation());
		matrices.scale(-0.025f, -0.025f, 0.025f);
		if (aoba.moduleManager.nametags.state.getValue()) {
			float scale;

			Nametags nameTagsModule = aoba.moduleManager.nametags;
			scale = (float) nameTagsModule.getNametagScale();
			matrices.scale(scale, scale, scale);
		}
		Matrix4f matrix4f = matrices.last().pose();
		float g = mc.options.getBackgroundOpacity(0.25f);
		int j = (int) (g * 255.0f) << 24;
		Font textRenderer = getFont();
		float h = (float) -textRenderer.width(text) / 2;
		textRenderer.drawInBatch(text, h, (float) i, 0x20FFFFFF, false, matrix4f, vertexConsumers,
				bl ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, light);
		if (bl) {
			textRenderer.drawInBatch(text, h, (float) i, -1, false, matrix4f, vertexConsumers,
					Font.DisplayMode.NORMAL, 0, light);
		}
		matrices.popPose();
	}
}
