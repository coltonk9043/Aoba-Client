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
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Inject(at = @At("TAIL"), method = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V")
	public void onRenderLevel(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline,
			Camera camera, Matrix4f viewMatrix, Matrix4f projectionMatrix, Matrix4f frustumMatrix,
			GpuBufferSlice bufferSlice, Vector4f fogColor, boolean isFoggy, CallbackInfo ci) {

		if (Aoba.getInstance().moduleManager != null) {
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			PoseStack matrixStack = new PoseStack();
			matrixStack.mulPose(viewMatrix);

			Frustum frustum = new Frustum(viewMatrix, projectionMatrix);
			frustum.prepare(camera.position().x, camera.position().y, camera.position().z);

			Render3DEvent renderEvent = new Render3DEvent(matrixStack, frustum, camera, tickCounter);
			Aoba.getInstance().eventManager.Fire(renderEvent);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		}
	}

	@Inject(at = @At("HEAD"), method = "doesMobEffectBlockSky(Lnet/minecraft/client/Camera;)Z", cancellable = true)
	private void onHasBlindnessOrDarknessEffect(Camera camera, CallbackInfoReturnable<Boolean> cir) {
		if (Aoba.getInstance().moduleManager.norender.state.getValue()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), method = "extractBlockOutline(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/state/LevelRenderState;)V", cancellable = true)
	private void onExtractBlockOutline(Camera camera, net.minecraft.client.renderer.state.LevelRenderState levelRenderState, CallbackInfo ci) {
		if (Aoba.getInstance().moduleManager.freecam.state.getValue())
			ci.cancel();
	}
}
