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

/**
 * A class that contains all of the drawing functions.
 */
package net.aoba.utils.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.aoba.gui.colors.Color;
import net.aoba.mixin.interfaces.ICuboid;
import net.aoba.mixin.interfaces.IModelPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.model.ModelPart.Quad;
import net.minecraft.client.model.ModelPart.Vertex;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
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
	public static void draw3DBox(MatrixStack matrixStack, Box box, Color color, float lineThickness) {
		RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();

		Tessellator tessellator = RenderSystem.renderThreadTesselator();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();

		RenderSystem.setShader(ShaderProgramKeys.POSITION);

		RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ);

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ);

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ);

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ);

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ);

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ);
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.setShaderColor(1, 1, 1, 1);

		RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);

		RenderSystem.lineWidth(lineThickness);

		bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

		buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX,
				(float) box.minY, (float) box.minZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.minZ, (float) box.maxX,
				(float) box.minY, (float) box.maxZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.maxZ, (float) box.minX,
				(float) box.minY, (float) box.maxZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.maxZ, (float) box.minX,
				(float) box.minY, (float) box.minZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.minZ, (float) box.minX,
				(float) box.maxY, (float) box.minZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.minZ, (float) box.maxX,
				(float) box.maxY, (float) box.minZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.minY, (float) box.maxZ, (float) box.maxX,
				(float) box.maxY, (float) box.maxZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.minY, (float) box.maxZ, (float) box.minX,
				(float) box.maxY, (float) box.maxZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.maxY, (float) box.minZ, (float) box.maxX,
				(float) box.maxY, (float) box.minZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.maxY, (float) box.minZ, (float) box.maxX,
				(float) box.maxY, (float) box.maxZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.maxX, (float) box.maxY, (float) box.maxZ, (float) box.minX,
				(float) box.maxY, (float) box.maxZ, color);
		buildLine3d(matrixStack, bufferBuilder, (float) box.minX, (float) box.maxY, (float) box.maxZ, (float) box.minX,
				(float) box.maxY, (float) box.minZ, color);

		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

		RenderSystem.enableCull();
		RenderSystem.lineWidth(1f);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	public static void drawLine3D(MatrixStack matrixStack, Vec3d pos1, Vec3d pos2, Color color, float lineWidth) {
		drawLine3D(matrixStack, (float) pos1.x, (float) pos1.y, (float) pos1.z, (float) pos2.x, (float) pos2.y,
				(float) pos2.z, color, lineWidth);
	}

	public static void drawLine3D(MatrixStack matrixStack, float x1, float y1, float z1, float x2, float y2, float z2,
			Color color, float lineWidth) {

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		RenderSystem.setShaderColor(1, 1, 1, 1);

		RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
		RenderSystem.lineWidth(lineWidth);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		buildLine3d(matrixStack, bufferBuilder, x1, y1, z1, x2, y2, z2, color);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.enableCull();
		RenderSystem.lineWidth(1f);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	public static void drawEntityModel(MatrixStack matrixStack, float partialTicks, Entity entity, Color color,
			float lineWidth) {
		EntityRenderer<?, ?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);

		if (entity instanceof LivingEntity) {
			matrixStack.push();

			LivingEntity livingEntity = (LivingEntity) entity;
			LivingEntityRenderer<?, ?, ?> leRenderer = (LivingEntityRenderer<?, ?, ?>) renderer;
			EntityModel<?> model = leRenderer.getModel();

			Direction sleepDirection = livingEntity.getSleepingDirection();
			// Setup transforms

			// model.handSwingProgress = livingEntity.getHandSwingProgress(partialTicks);
			// model.riding = livingEntity.hasVehicle();
			// model.child = livingEntity.isBaby();

			// Interpolate entity position and body rotations.
			Vec3d interpolatedEntityPosition = getEntityPositionInterpolated(entity, partialTicks);
			float interpolatedBodyYaw = MathHelper.lerpAngleDegrees(partialTicks, livingEntity.prevBodyYaw,
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

			// Render Vertices
			Tessellator tessellator = RenderSystem.renderThreadTesselator();

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableCull();
			RenderSystem.disableDepthTest();

			RenderSystem.setShader(ShaderProgramKeys.POSITION);
			RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

			// Draw Vertices
			boolean hasVertices = false;
			BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
			Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

			for (ModelPart part : model.getParts()) {
				hasVertices |= buildModelPartVertices(matrix4f, entity, part, bufferBuilder);
			}

			if (hasVertices)
				BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderSystem.enableCull();
			RenderSystem.lineWidth(1f);
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();

			matrixStack.pop();
		}
	}

	private static float getYaw(Direction direction) {
		switch (direction) {
		case SOUTH: {
			return 90.0f;
		}
		case WEST: {
			return 0.0f;
		}
		case NORTH: {
			return 270.0f;
		}
		case EAST: {
			return 180.0f;
		}
		default:
			return 0.0f;
		}
	}

	private static boolean buildModelPartVertices(Matrix4f matrix4f, Entity entity, ModelPart part,
			BufferBuilder bufferBuilder) {
		if (!part.visible)
			return false;

		// ModelPart is a final class so it cannot be cast to an IModelPart.
		// Casting it to an object tricks the compiler to cast it.
		IModelPart iModelPart = (IModelPart) (Object) part;

		MatrixStack modelMatrixStack = new MatrixStack();
		part.rotate(modelMatrixStack);

		boolean result = false;
		for (Cuboid cuboid : iModelPart.getCuboids()) {
			result |= renderCuboid(matrix4f, modelMatrixStack.peek().getPositionMatrix(), bufferBuilder, cuboid);
		}

		for (ModelPart child : iModelPart.getChildren().values()) {
			result |= buildModelPartVertices(matrix4f, entity, child, bufferBuilder);
		}

		return result;
	}

	private static boolean renderCuboid(Matrix4f transformation, Matrix4f partTransform, BufferBuilder bufferBuilder,
			Cuboid cuboid) {
		boolean result = false;
		Vector3f vector3f = new Vector3f();
		ICuboid iCuboid = (ICuboid) cuboid;
		for (Quad quad : iCuboid.getSides()) {
			for (Vertex vertex : quad.vertices()) {
				float i = vertex.pos().x() / 16.0f;
				float j = vertex.pos().y() / 16.0f;
				float k = vertex.pos().z() / 16.0f;
				partTransform.transformPosition(i, j, k, vector3f);
				bufferBuilder.vertex(transformation, vector3f.x(), vector3f.y(), vector3f.z());
				result |= true;
			}
		}
		return result;
	}

	private static void buildLine3d(MatrixStack matrixStack, BufferBuilder bufferBuilder, float x1, float y1, float z1,
			float x2, float y2, float z2, Color color) {
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();

		Vec3d normalized = new Vec3d(x2 - x1, y2 - y1, z2 - z1).normalize();

		float r = color.getRed();
		float g = color.getGreen();
		float b = color.getBlue();

		bufferBuilder.vertex(matrix4f, x1, y1, z1).color(r, g, b, 1.0f).normal(entry, (float) normalized.x,
				(float) normalized.y, (float) normalized.z);
		bufferBuilder.vertex(matrix4f, x2, y2, z2).color(r, g, b, 1.0f).normal(entry, (float) normalized.x,
				(float) normalized.y, (float) normalized.z);
	}

	/**
	 * Gets the interpolated position of the entity given a tick delta.
	 *
	 * @param entity Entity to get position of
	 * @param delta  Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3d getEntityPositionInterpolated(Entity entity, float delta) {
		return new Vec3d(MathHelper.lerp(delta, entity.prevX, entity.getX()),
				MathHelper.lerp(delta, entity.prevY, entity.getY()),
				MathHelper.lerp(delta, entity.prevZ, entity.getZ()));
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
