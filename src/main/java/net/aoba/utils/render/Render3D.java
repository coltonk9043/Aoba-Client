/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.render;

import static net.aoba.AobaClient.MC;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.joml.Matrix4f;

import net.aoba.gui.colors.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Render3D {
	public static void draw3DBox(PoseStack matrixStack, Camera camera, AABB box, Color color, float lineThickness) {
		AABB newBox = box.move(camera.position().scale(-1));

		PoseStack.Pose entry = matrixStack.last();
		Matrix4f matrix4f = entry.pose();

		float r = color.getRed();
		float g = color.getGreen();
		float b = color.getBlue();
		float a = color.getAlpha();

		// Drawing Logic
		MultiBufferSource.BufferSource vertexConsumerProvider = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.QUADS;
		VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(layer);

		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.maxZ).setColor(r, g, b, a);

		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.minZ).setColor(r, g, b, a);

		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.minZ).setColor(r, g, b, a);

		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.maxZ).setColor(r, g, b, a);

		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.maxZ).setColor(r, g, b, a);

		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.minZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.maxZ).setColor(r, g, b, a);
		bufferBuilder.addVertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.minZ).setColor(r, g, b, a);

		vertexConsumerProvider.endBatch(layer);

		layer = RenderLayers.LINES;
		bufferBuilder = vertexConsumerProvider.getBuffer(layer);

		buildLine3d(matrixStack, camera, bufferBuilder, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ,
				color);
		buildLine3d(matrixStack, camera, bufferBuilder, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ,
				color);

		vertexConsumerProvider.endBatch(layer);

		// RenderSystem.enableCull();
		// RenderSystem.lineWidth(1f);
		// RenderSystem.enableDepthTest();
		// RenderSystem.disableBlend();
	}

	public static void drawLine3D(PoseStack matrixStack, Camera camera, Vec3 pos1, Vec3 pos2, Color color) {
		drawLine3D(matrixStack, camera, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, color);
	}

	public static void drawLine3D(PoseStack matrixStack, Camera camera, double x1, double y1, double z1, double x2,
			double y2, double z2, Color color) {
		MultiBufferSource.BufferSource vertexConsumerProvider = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.LINES;
		VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(layer);
		buildLine3d(matrixStack, camera, bufferBuilder, x1, y1, z1, x2, y2, z2, color);
		vertexConsumerProvider.endBatch(layer);
	}

	@SuppressWarnings("unchecked")
	public static void drawEntityModel(PoseStack matrixStack, Camera camera, float partialTicks, Entity entity,
			Color color) {
		EntityRenderer<?, ?> renderer = MC.getEntityRenderDispatcher().getRenderer(entity);

		if (entity instanceof LivingEntity livingEntity) {
			matrixStack.pushPose();

			LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> leRenderer = (LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>>) renderer;
			EntityModel<LivingEntityRenderState> model = leRenderer.getModel();
			LivingEntityRenderState renderState = leRenderer.createRenderState(livingEntity, partialTicks);
			renderState.isBaby = livingEntity.isBaby();
			model.setupAnim(renderState);
			Direction sleepDirection = livingEntity.getBedOrientation();

			// Interpolate entity position and body rotations.
			Vec3 interpolatedEntityPosition = getEntityPositionInterpolated(entity, partialTicks)
					.add(camera.position().scale(-1));
			float interpolatedBodyYaw = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO,
					livingEntity.yBodyRot);
			// Translate by the entity's interpolated position.
			matrixStack.translate(interpolatedEntityPosition.x(), interpolatedEntityPosition.y(),
					interpolatedEntityPosition.z());

			// If entity is sleeping, move their render position by their sleeping offset.
			if (livingEntity.hasPose(Pose.SLEEPING) && sleepDirection != null) {
				float sleepingEyeHeight = livingEntity.getEyeHeight(Pose.STANDING) - 0.1f;
				matrixStack.translate(-sleepDirection.getStepX() * sleepingEyeHeight, 0.0f,
						-sleepDirection.getStepZ() * sleepingEyeHeight);
			}

			// Scale by the entity's scale.
			float entityScale = livingEntity.getScale();
			matrixStack.scale(entityScale, entityScale, entityScale);

			// If Entity is frozen (similar to shaking from zombie conversion shakes.
			if (entity.isFullyFrozen()) {
				interpolatedBodyYaw += (float) (Math.cos((livingEntity.tickCount * 3.25) * Math.PI * 0.4f));
			}

			// Rotate entity if they are sleeping.
			if (!livingEntity.hasPose(Pose.SLEEPING)) {
				matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f - interpolatedBodyYaw));
			}

			// Check for rotations based off of the entity's state (dead, alive, sleeping,
			// using riptide?, etc...)
			if (livingEntity.deathTime > 0) {
				float dyingAngle = Mth.sqrt((livingEntity.deathTime + partialTicks - 1.0f) / 20.0f * 1.6f);
				if (dyingAngle > 1.0f) {
					dyingAngle = 1.0f;
				}

				matrixStack.mulPose(Axis.ZP.rotationDegrees(dyingAngle * 90f));
			} else if (livingEntity.isAutoSpinAttack()) {
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90.0f - livingEntity.getXRot()));
				matrixStack
						.mulPose(Axis.YP.rotationDegrees((livingEntity.tickCount + partialTicks) * -75.0f));
			} else if (livingEntity.hasPose(Pose.SLEEPING)) {
				float sleepAngle = sleepDirection != null ? getYaw(sleepDirection) : interpolatedBodyYaw;
				matrixStack.mulPose(Axis.YP.rotationDegrees(sleepAngle));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
				matrixStack.mulPose(Axis.YP.rotationDegrees(270.0f));
			}

			// Apply offset for correct rendering on screen. (Not sure why though!)
			matrixStack.scale(-1.0f, -1.0f, 1.0f);
			matrixStack.translate(0.0f, -1.501f, 0.0f);
			MultiBufferSource.BufferSource vertexConsumerProvider = MC.renderBuffers().bufferSource();
			RenderType layer = RenderLayers.QUADS;
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(layer);
			model.renderToBuffer(matrixStack, bufferBuilder, 0, 0, color.getColorAsInt());
			vertexConsumerProvider.endBatch(layer);
			matrixStack.popPose();
		}
	}

	private static float getYaw(Direction direction) {
		return switch (direction) {
		case SOUTH -> 90.0f;
		case WEST -> 0.0f;
		case NORTH -> 270.0f;
		case EAST -> 180.0f;
		default -> 0.0f;
		};
	}

	private static void buildLine3d(PoseStack matrixStack, Camera camera, VertexConsumer bufferBuilder, double x1,
			double y1, double z1, double x2, double y2, double z2, Color color) {
		PoseStack.Pose entry = matrixStack.last();
		Matrix4f matrix4f = entry.pose();
		Vec3 cameraPos = camera.position();

		float r = color.getRed();
		float g = color.getGreen();
		float b = color.getBlue();

		bufferBuilder
				.addVertex(matrix4f, (float) (x1 - cameraPos.x), (float) (y1 - cameraPos.y), (float) (z1 - cameraPos.z))
				.setColor(r, g, b, 1.0f);
		bufferBuilder
				.addVertex(matrix4f, (float) (x2 - cameraPos.x), (float) (y2 - cameraPos.y), (float) (z2 - cameraPos.z))
				.setColor(r, g, b, 1.0f);
	}

	/**
	 * Gets the interpolated position of the entity given a tick delta.
	 *
	 * @param entity Entity to get position of
	 * @param delta  Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3 getEntityPositionInterpolated(Entity entity, float delta) {
		return new Vec3(Mth.lerp(delta, entity.xo, entity.getX()),
				Mth.lerp(delta, entity.yo, entity.getY()),
				Mth.lerp(delta, entity.zo, entity.getZ()));
	}

	/**
	 * Gets the difference between the interpolated position and
	 *
	 * @param entity Entity to get position of
	 * @param delta  Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3 getEntityPositionOffsetInterpolated(Entity entity, float delta) {
		Vec3 interpolated = getEntityPositionInterpolated(entity, delta);
		return entity.position().subtract(interpolated);
	}
}
