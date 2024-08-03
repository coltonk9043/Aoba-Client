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

import com.mojang.blaze3d.systems.RenderSystem;
import net.aoba.gui.colors.Color;
import net.aoba.mixin.interfaces.ICuboid;
import net.aoba.mixin.interfaces.IModelPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.model.ModelPart.Quad;
import net.minecraft.client.model.ModelPart.Vertex;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Render3D {
	public static void draw3DBox(MatrixStack matrixStack, Box box, Color color, float lineThickness) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();

		Tessellator tessellator = RenderSystem.renderThreadTesselator();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();

		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

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

		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
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

		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(lineWidth);

		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		buildLine3d(matrixStack, bufferBuilder, x1, y1, z1, x2, y2, z2, color);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.enableCull();
		RenderSystem.lineWidth(1f);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	public static void drawEntityModel(MatrixStack matrixStack, Entity entity, Color color, float lineWidth) {
		EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);


		Vec3d entityRotation = entity.getRotationVector();
		
		matrixStack.push();
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();
		matrixStack.translate(entity.getX(), entity.getY(), entity.getZ());
		matrixStack.multiply(new Quaternionf().rotationZYX((float)Math.toRadians(180), (float)Math.toRadians(entityRotation.y), (float)Math.toRadians(entityRotation.x)));
		Tessellator tessellator = RenderSystem.renderThreadTesselator();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();

		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());

		if (renderer instanceof LivingEntityRenderer) {
			LivingEntityRenderer<?, ?> leRenderer = (LivingEntityRenderer<?, ?>) renderer;
			EntityModel<?> model = leRenderer.getModel();

			if (model instanceof SinglePartEntityModel) {

				SinglePartEntityModel<?> singleModel = (SinglePartEntityModel<?>) model;
				ModelPart root = singleModel.getPart();

				BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
				boolean hasVertices = buildModelPartVertices(matrix4f, new MatrixStack(), entity, root,
						bufferBuilder);

				if (hasVertices)
					BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			}
		}

		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableCull();
		RenderSystem.lineWidth(1f);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		matrixStack.pop();
	}

	private static boolean buildModelPartVertices(Matrix4f matrix4f, MatrixStack modelMatrixStack, Entity entity,
			ModelPart part, BufferBuilder bufferBuilder) {
		// ModelPart is a final class so it cannot be cast to an IModelPart.
		// Casting it to an object tricks the compiler to cast it.
		IModelPart iModelPart = (IModelPart) (Object) part;

		modelMatrixStack.push();
		modelMatrixStack.translate(part.pivotX / 16.0f, part.pivotY / 16.0f, part.pivotZ / 16.0f);
		if (part.pitch != 0.0f || part.yaw != 0.0f || part.roll != 0.0f) {
			modelMatrixStack.multiply(new Quaternionf().rotationZYX(part.roll, part.yaw, part.pitch));
		}
		if (part.xScale != 1.0f || part.yScale != 1.0f || part.zScale != 1.0f) {
			modelMatrixStack.scale(part.xScale, part.yScale, part.zScale);
		}

		boolean result = false;

		MatrixStack.Entry entry = modelMatrixStack.peek();
		for (Cuboid cuboid : iModelPart.getCuboids()) {
			result |= renderCuboid(matrix4f, entry, bufferBuilder, cuboid);
		}

		for (ModelPart child : iModelPart.getChildren().values()) {
			result |= buildModelPartVertices(matrix4f, modelMatrixStack, entity, child, bufferBuilder);
		}

		modelMatrixStack.pop();

		return result;
	}
	
	private static boolean renderCuboid(Matrix4f transformation, MatrixStack.Entry entry, BufferBuilder bufferBuilder, Cuboid cuboid) {
		boolean result = false;
        Matrix4f matrix4f = entry.getPositionMatrix();
        Vector3f vector3f = new Vector3f();
        ICuboid iCuboid = (ICuboid) cuboid;
        for (Quad quad : iCuboid.getSides()) {
            Vector3f vector3f2 = entry.transformNormal(quad.direction, vector3f);
            float f = vector3f2.x();
            float g = vector3f2.y();
            float h = vector3f2.z();
            for (Vertex vertex : quad.vertices) {
                float i = vertex.pos.x() / 16.0f;
                float j = vertex.pos.y() / 16.0f;
                float k = vertex.pos.z() / 16.0f;
                Vector3f vector3f3 = matrix4f.transformPosition(i, j, k, vector3f);
                bufferBuilder.vertex(transformation, vector3f3.x(), vector3f3.y(), vector3f3.z()).normal(f, g, h);
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

		float r = color.getRedFloat();
		float g = color.getGreenFloat();
		float b = color.getBlueFloat();

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
