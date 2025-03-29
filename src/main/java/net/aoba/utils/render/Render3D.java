/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.render;

import static net.aoba.AobaClient.MC;

import org.joml.Matrix4f;

import net.aoba.gui.colors.Color;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class Render3D {
	public static void draw3DBox(MatrixStack matrixStack, Camera camera, Box box, Color color, float lineThickness) {
		Box newBox = box.offset(camera.getPos().multiply(-1));

		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();

		float r = color.getRed();
		float g = color.getGreen();
		float b = color.getBlue();
		float a = color.getAlpha();

		// Drawing Logic
		VertexConsumerProvider.Immediate vertexConsumerProvider = MC.getBufferBuilders().getEntityVertexConsumers();
		RenderLayer layer = RenderLayers.QUADS;
		VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(layer);

		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.maxZ).color(r, g, b, a);

		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.minZ).color(r, g, b, a);

		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.minZ).color(r, g, b, a);

		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.maxZ).color(r, g, b, a);

		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.minY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.maxX, (float) newBox.maxY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.maxZ).color(r, g, b, a);

		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.minZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.minY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.maxZ).color(r, g, b, a);
		bufferBuilder.vertex(matrix4f, (float) newBox.minX, (float) newBox.maxY, (float) newBox.minZ).color(r, g, b, a);

		vertexConsumerProvider.draw(layer);

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

		vertexConsumerProvider.draw(layer);

		// RenderSystem.enableCull();
		// RenderSystem.lineWidth(1f);
		// RenderSystem.enableDepthTest();
		// RenderSystem.disableBlend();
	}

	public static void drawLine3D(MatrixStack matrixStack, Camera camera, Vec3d pos1, Vec3d pos2, Color color) {
		drawLine3D(matrixStack, camera, pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, color);
	}

	public static void drawLine3D(MatrixStack matrixStack, Camera camera, double x1, double y1, double z1, double x2,
			double y2, double z2, Color color) {
		VertexConsumerProvider.Immediate vertexConsumerProvider = MC.getBufferBuilders().getEntityVertexConsumers();
		RenderLayer layer = RenderLayers.LINES;
		VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(layer);
		buildLine3d(matrixStack, camera, bufferBuilder, x1, y1, z1, x2, y2, z2, color);
		vertexConsumerProvider.draw(layer);
	}

	@SuppressWarnings("unchecked")
	public static void drawEntityModel(MatrixStack matrixStack, Camera camera, float partialTicks, Entity entity,
			Color color) {
		EntityRenderer<?, ?> renderer = MC.getEntityRenderDispatcher().getRenderer(entity);

		if (entity instanceof LivingEntity livingEntity) {
			matrixStack.push();

			LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> leRenderer = (LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>>) renderer;
			EntityModel<LivingEntityRenderState> model = leRenderer.getModel();
			LivingEntityRenderState renderState = leRenderer.getAndUpdateRenderState(livingEntity, partialTicks);
			renderState.baby = livingEntity.isBaby();
			model.setAngles(renderState);
			Direction sleepDirection = livingEntity.getSleepingDirection();

			// Interpolate entity position and body rotations.
			Vec3d interpolatedEntityPosition = getEntityPositionInterpolated(entity, partialTicks)
					.add(camera.getPos().multiply(-1));
			float interpolatedBodyYaw = MathHelper.lerpAngleDegrees(partialTicks, livingEntity.lastBodyYaw,
					livingEntity.bodyYaw);
			// Translate by the entity's interpolated position.
			matrixStack.translate(interpolatedEntityPosition.getX(), interpolatedEntityPosition.getY(),
					interpolatedEntityPosition.getZ());

			// If entity is sleeping, move their render position by their sleeping offset.
			if (livingEntity.isInPose(EntityPose.SLEEPING) && sleepDirection != null) {
				float sleepingEyeHeight = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1f;
				matrixStack.translate(-sleepDirection.getOffsetX() * sleepingEyeHeight, 0.0f,
						-sleepDirection.getOffsetZ() * sleepingEyeHeight);
			}

			// Scale by the entity's scale.
			float entityScale = livingEntity.getScale();
			matrixStack.scale(entityScale, entityScale, entityScale);

			// If Entity is frozen (similar to shaking from zombie conversion shakes.
			if (entity.isFrozen()) {
				interpolatedBodyYaw += (float) (Math.cos((livingEntity.age * 3.25) * Math.PI * 0.4f));
			}

			// Rotate entity if they are sleeping.
			if (!livingEntity.isInPose(EntityPose.SLEEPING)) {
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - interpolatedBodyYaw));
			}

			// Check for rotations based off of the entity's state (dead, alive, sleeping,
			// using riptide?, etc...)
			if (livingEntity.deathTime > 0) {
				float dyingAngle = MathHelper.sqrt((livingEntity.deathTime + partialTicks - 1.0f) / 20.0f * 1.6f);
				if (dyingAngle > 1.0f) {
					dyingAngle = 1.0f;
				}

				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(dyingAngle * 90f));
			} else if (livingEntity.isUsingRiptide()) {
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - livingEntity.getPitch()));
				matrixStack
						.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((livingEntity.age + partialTicks) * -75.0f));
			} else if (livingEntity.isInPose(EntityPose.SLEEPING)) {
				float sleepAngle = sleepDirection != null ? getYaw(sleepDirection) : interpolatedBodyYaw;
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(sleepAngle));
				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
			}

			// Apply offset for correct rendering on screen. (Not sure why though!)
			matrixStack.scale(-1.0f, -1.0f, 1.0f);
			matrixStack.translate(0.0f, -1.501f, 0.0f);
			VertexConsumerProvider.Immediate vertexConsumerProvider = MC.getBufferBuilders().getEntityVertexConsumers();
			RenderLayer layer = RenderLayers.QUADS;
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(layer);
			model.render(matrixStack, bufferBuilder, 0, 0, color.getColorAsInt());
			vertexConsumerProvider.draw(layer);
			matrixStack.pop();
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

	private static void buildLine3d(MatrixStack matrixStack, Camera camera, VertexConsumer bufferBuilder, double x1,
			double y1, double z1, double x2, double y2, double z2, Color color) {
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();
		Vec3d cameraPos = camera.getPos();
		Vec3d normalized = new Vec3d(x2 - x1, y2 - y1, z2 - z1).normalize();

		float r = color.getRed();
		float g = color.getGreen();
		float b = color.getBlue();

		bufferBuilder
				.vertex(matrix4f, (float) (x1 - cameraPos.x), (float) (y1 - cameraPos.y), (float) (z1 - cameraPos.z))
				.color(r, g, b, 1.0f).normal(entry, (float) normalized.x, (float) normalized.y, (float) normalized.z);
		bufferBuilder
				.vertex(matrix4f, (float) (x2 - cameraPos.x), (float) (y2 - cameraPos.y), (float) (z2 - cameraPos.z))
				.color(r, g, b, 1.0f).normal(entry, (float) normalized.x, (float) normalized.y, (float) normalized.z);
	}

	/**
	 * Gets the interpolated position of the entity given a tick delta.
	 *
	 * @param entity Entity to get position of
	 * @param delta  Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3d getEntityPositionInterpolated(Entity entity, float delta) {
		return new Vec3d(MathHelper.lerp(delta, entity.lastX, entity.getX()),
				MathHelper.lerp(delta, entity.lastY, entity.getY()),
				MathHelper.lerp(delta, entity.lastZ, entity.getZ()));
	}

	/**
	 * Gets the difference between the interpolated position and
	 *
	 * @param entity Entity to get position of
	 * @param delta  Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3d getEntityPositionOffsetInterpolated(Entity entity, float delta) {
		Vec3d interpolated = getEntityPositionInterpolated(entity, delta);
		return entity.getPos().subtract(interpolated);
	}
}
