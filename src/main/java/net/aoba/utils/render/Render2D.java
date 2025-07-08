/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.render;

import org.joml.Matrix4f;

import net.aoba.Aoba;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.utils.render.mesh.MeshRenderer;
import net.aoba.utils.render.mesh.builders.LineMeshBuilder;
import net.aoba.utils.render.mesh.builders.TriMeshBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class Render2D {

	public static MinecraftClient mc = MinecraftClient.getInstance();

	public final TriMeshBuilder triangles;
	public final LineMeshBuilder lines;

	public Render2D() {
		triangles = new TriMeshBuilder(AobaRenderPipelines.TRIS_GUI);
		lines = new LineMeshBuilder(AobaRenderPipelines.LINES_GUI);
	}

	public void begin() {
		triangles.begin();
		lines.begin();
	}

	public void end() {
		triangles.end();
		lines.end();
		render();
	}

	private void render() {
		MeshRenderer.begin().withFramebuffer(MinecraftClient.getInstance().getFramebuffer())
				.withPipeline(AobaRenderPipelines.TRIS_GUI).withMesh(triangles).end();

		MeshRenderer.begin().withFramebuffer(MinecraftClient.getInstance().getFramebuffer())
				.withPipeline(AobaRenderPipelines.LINES_GUI).withMesh(lines).end();
	}

	private static void bobView(MatrixStack matrices) {
		Entity cameraEntity = MinecraftClient.getInstance().getCameraEntity();

		if (cameraEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
			float tickDelta = mc.getRenderTickCounter().getTickProgress(true);

			float var7 = abstractClientPlayerEntity.distanceMoved - abstractClientPlayerEntity.lastDistanceMoved;
			float g = -(abstractClientPlayerEntity.distanceMoved + var7 * tickDelta);
			float h = MathHelper.lerp(tickDelta, abstractClientPlayerEntity.lastStrideDistance,
					abstractClientPlayerEntity.strideDistance);

			matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F,
					-Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3f));
			matrices.multiply(RotationAxis.POSITIVE_X
					.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2f) * h) * 5f));
		}
	}

	/**
	 ** FILLED BOXES
	 **/

	/**
	 * Draws a textured quad onto the screen.
	 *
	 * @param matrix4f Transformation matrix.
	 * @param texture  Texture identifier to draw.
	 * @param size     Size and position of the quad to draw.
	 * @param color    Color to overlay on top of the quad.
	 */
	public void drawTexturedQuad(DrawContext drawContext, Identifier texture, Rectangle size, Color color) {
		drawTexturedQuad(drawContext, texture, size.getX(), size.getY(), size.getWidth(), size.getHeight(), color);
	}

	/**
	 * Draws a textured quad onto the screen.
	 *
	 * @param matrix4f Transformation matrix.
	 * @param texture
	 * @param x1       X position to draw the quad.
	 * @param y1       Y position to draw the quad.
	 * @param width    Width of the quad.
	 * @param height   Height of the quad.
	 * @param color    Color to overlay on top of the quad.
	 */
	public void drawTexturedQuad(DrawContext drawContext, Identifier texture, float x1, float y1, float width,
			float height, Color color) {
//		MatrixStack matrixStack = drawContext.getMatrices();
//		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
//		int colorInt = color.getColorAsInt();
//
//		float x2 = x1 + width;
//		float y2 = y1 + height;
//
//		drawContext.draw
//		drawContext.draw(vertexConsumerProvider -> {
//			VertexConsumer bufferBuilder = vertexConsumerProvider
//					.getBuffer(RenderLayers.TEXTURES_QUADS_GUI.apply(texture));
//			bufferBuilder.vertex(matrix4f, x1, y1, 0).color(colorInt).texture(0, 0);
//			bufferBuilder.vertex(matrix4f, x1, y2, 0).color(colorInt).texture(0, 1);
//			bufferBuilder.vertex(matrix4f, x2, y2, 0).color(colorInt).texture(1, 1);
//			bufferBuilder.vertex(matrix4f, x2, y1, 0).color(colorInt).texture(1, 0);
//		});
	}

	/**
	 * Draws a box on the screen.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size and position of the box to draw.
	 * @param color    Color of the box.
	 */
	public void drawBox(DrawContext drawContext, Rectangle size, Color color) {
		drawBox(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), color);
	}

	/**
	 * Draws a filled box on the screen.
	 *
	 * @param matrix4f Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param width    Width of the box.
	 * @param height   Height of the box.
	 * @param color    Color of the box.
	 */
	public void drawBox(DrawContext drawContext, float x, float y, float width, float height, Color color) {
		triangles.triangle(triangles.vec2d(x, y).color(color).next(), triangles.vec2d(x + width, y).color(color).next(),
				triangles.vec2d(x, y + height).color(color).next());

		triangles.triangle(triangles.vec2d(x, y + height).color(color).next(),
				triangles.vec2d(x + width, y).color(color).next(),
				triangles.vec2d(x + width, y + height).color(color).next());
	}

	/**
	 * Draws a filled rounded box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size and position of the rounded box.
	 * @param radius   Radius of the box corners.
	 * @param color    Color of the box.
	 */
	public void drawRoundedBox(DrawContext drawContext, Rectangle size, float radius, Color color) {
		drawRoundedBox(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), radius, color);
	}

	/**
	 * Draws a filled rounded box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param width    Width of the box.
	 * @param height   Height of the box.
	 * @param radius   Radius of the box corners.
	 * @param color    Color of the box.
	 */
	public void drawRoundedBox(DrawContext drawContext, float x, float y, float width, float height, float radius,
			Color color) {
		if (radius == 0) {
			drawBox(drawContext, x, y, width, height, color);
		} else {
			drawBox(drawContext, x, y, width, height, color);
//			int colorInt = color.getColorAsInt();
			//
//					MatrixStack matrixStack = drawContext.getMatrices();
//					Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
			//
//					drawContext.draw(vertexConsumerProvider -> {
//						VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.TRIS_GUI);
//						buildFilledArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, color);
//						buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f, color);
//						buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f,
//								color);
//						buildFilledArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f, color);
			//
//						// |---
//						bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);
			//
//						// ---|
//						bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);
			//
//						// _||
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);
			//
//						// |||
//						bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);
			//
//						/// __|
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
			//
//						// |__
//						bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(colorInt);
			//
//						// |||
//						bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(colorInt);
			//
//						/// ||-
//						bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
			//
//						/// |-/
//						bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
			//
//						/// /_|
//						bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);
//						bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);
//					});
		}
	}

	/**
	 * Draws a filled circle.
	 *
	 * @param matrix4f Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param radius   Radius of the circle.
	 * @param color    Color of the box.
	 */
	public void drawCircle(DrawContext drawContext, float x, float y, float radius, Color color) {

		double roundedInterval = (360.0f / 30.0f);

		for (int i = 0; i < 30; i++) {
			double angle = Math.toRadians(0 + (i * roundedInterval));
			double angle2 = Math.toRadians(0 + ((i + 1) * roundedInterval));
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float) Math.sin(angle) * radius;
			float radiusX2 = (float) Math.cos(angle2) * radius;
			float radiusY2 = (float) Math.sin(angle2) * radius;

			triangles.triangle(triangles.vec2d(x, y).color(color).next(),
					triangles.vec2d(x + radiusX1, y + radiusY1).color(color).next(),
					triangles.vec2d(x + radiusX2, y + radiusY2).color(color).next());
		}
	}

	/**
	 * Draws a filled AND outlined box.
	 *
	 * @param matrix4f        Transformation matrix
	 * @param size            Size and position to draw the outlined box.
	 * @param outlineColor    Color of the outline of the box.
	 * @param backgroundColor Color of the fill.
	 */
	public void drawOutlinedBox(DrawContext drawContext, Rectangle size, Color outlineColor, Color backgroundColor) {
		drawOutlinedBox(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), outlineColor,
				backgroundColor);
	}

	/**
	 * Draws a filled AND outlined box.
	 *
	 * @param matrix4f        Transformation matrix
	 * @param x               X position of the box.
	 * @param y               Y position of the box.
	 * @param width           Width of the box.
	 * @param height          Height of the box.
	 * @param outlineColor    Color of the outline of the box.
	 * @param backgroundColor Color of the fill.
	 */
	public void drawOutlinedBox(DrawContext drawContext, float x, float y, float width, float height,
			Color outlineColor, Color backgroundColor) {

		drawBox(drawContext, x, y, width, height, backgroundColor);
		drawBoxOutline(drawContext, x, y, width, height, outlineColor);
	}

	/**
	 ** OUTLINES
	 **/

	/**
	 * Draws the outline of a box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size and position of the box.
	 * @param color    Color of the box.
	 */
	public void drawBoxOutline(DrawContext drawContext, Rectangle size, Color color) {
		drawBoxOutline(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), color);
	}

	/**
	 * Draws the outline of a box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param width    Width of the box.
	 * @param height   Height of the box.
	 * @param color    Color of the box.
	 */
	public void drawBoxOutline(DrawContext drawContext, float x, float y, float width, float height, Color color) {
		lines.line(lines.vec2d(x, y).color(color).next(), lines.vec2d(x + width, y).color(color).next());

		lines.line(lines.vec2d(x + width, y).color(color).next(),
				lines.vec2d(x + width, y + height).color(color).next());

		lines.line(lines.vec2d(x + width, y + height).color(color).next(),
				lines.vec2d(x, y + height).color(color).next());

		lines.line(lines.vec2d(x, y + height).color(color).next(), lines.vec2d(x, y).color(color).next());
	}

	/**
	 * Draws the outline of a rounded box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size of the rounded box.
	 * @param radius   Corner radius of the box outline.
	 * @param color    Color of the outline of the box.
	 */
	public void drawRoundedBoxOutline(DrawContext drawContext, Rectangle size, float radius, Color color) {
		drawRoundedBoxOutline(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), radius, color);
	}

	/**
	 * Draws the outline of a rounded box.
	 *
	 * @param matrix4f        Transformation matrix
	 * @param x               X position of the box.
	 * @param y               Y position of the box.
	 * @param width           Width of the box.
	 * @param height          Height of the box.
	 * @param radius          Corner radius of the box outline.
	 * @param outlineColor    Color of the outline of the box.
	 * @param backgroundColor Color of the background of the box.
	 */
	public void drawOutlinedRoundedBox(DrawContext drawContext, float x, float y, float width, float height,
			float radius, Color outlineColor, Color backgroundColor) {
		drawOutlinedBox(drawContext, x, y, width, height, outlineColor, backgroundColor);

//
//		MatrixStack matrixStack = drawContext.getMatrices();
//		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
//		int backgroundColorInt = backgroundColor.getColorAsInt();
//		int outlineColorInt = outlineColor.getColorAsInt();
//		GL11.glEnable(GL11.GL_LINE_SMOOTH);
//		drawContext.draw(vertexConsumerProvider -> {
//			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.TRIS_GUI);
//
//			//
//			buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f,
//					backgroundColor);
//			buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f,
//					backgroundColor);
//			buildFilledArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f,
//					backgroundColor);
//			buildFilledArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, backgroundColor);
//
//			// |---
//			bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);
//
//			// ---|
//
//			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);
//
//			// _||
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);
//
//			// |||
//			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);
//
//			/// __|
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
//
//			// |__
//			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(backgroundColorInt);
//
//			// |||
//			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(backgroundColorInt);
//
//			/// ||-
//			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
//
//			/// |-/
//			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
//
//			/// /_|
//			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);
//
//			bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.LINES_GUI);
//			// Top Left Arc and Top
//			buildArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, outlineColor);
//			bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(outlineColorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(outlineColorInt);
//
//			// Top Right Arc and Right
//			buildArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f, outlineColor);
//			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(outlineColorInt);
//			bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(outlineColorInt);
//
//			// Bottom Right
//			buildArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f,
//					outlineColor);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(outlineColorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(outlineColorInt);
//
//			// Bottom Left
//			buildArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f, outlineColor);
//			bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(outlineColorInt);
//			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(outlineColorInt);
//		});
//		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	/**
	 * Draws the outline of a rounded box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param width    Width of the box.
	 * @param height   Height of the box.
	 * @param radius   Corner radius of the box outline.
	 * @param color    Color of the outline of the box.
	 */
	public void drawRoundedBoxOutline(DrawContext drawContext, float x, float y, float width, float height,
			float radius, Color color) {

		drawBoxOutline(drawContext, x, y, width, height, color);

//		MatrixStack matrixStack = drawContext.getMatrices();
//		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
//		int colorInt = color.getColorAsInt();
//		GL11.glEnable(GL11.GL_LINE_SMOOTH);
//		drawContext.draw(vertexConsumerProvider -> {
//			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.LINES_GUI);
//			// Top Left Arc and Top
//			buildArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, color);
//			bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(colorInt);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(colorInt);
//
//			// Top Right Arc and Right
//			buildArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f, color);
//			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(colorInt);
//			bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(colorInt);
//
//			// Bottom Right
//			buildArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f, color);
//			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(colorInt);
//			bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(colorInt);
//
//			// Bottom Left
//			buildArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f, color);
//			bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(colorInt);
//			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(colorInt);
//		});
//		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	/**
	 * Draws a line from Point A to Point B
	 *
	 * @param matrix4f Transformation matrix
	 * @param x1       X position of the first line.
	 * @param y1       Y position of the first line.
	 * @param x1       X position of the second line.
	 * @param y1       Y position of the second line.
	 * @param color    Color to draw the line in.
	 */
	public void drawLine(DrawContext drawContext, float x1, float y1, float x2, float y2, Color color) {
		lines.line(lines.vec2d(x1, y1).color(color).next(), lines.vec2d(x2, y2).color(color).next());
	}

	/**
	 ** GRADIENTS
	 **/

	/**
	 * Draws a horizontal gradient within a box.
	 *
	 * @param matrix4f   Transformation matrix.
	 * @param size       Size and position of the gradient.
	 * @param startColor The start color of the gradient.
	 * @param endColor   The end color of the gradient.
	 */
	public void drawHorizontalGradient(DrawContext drawContext, Rectangle size, Color startColor, Color endColor) {
		drawHorizontalGradient(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), startColor,
				endColor);
	}

	/**
	 * Draws a horizontal gradient within a box.
	 *
	 * @param matrix4f   Transformation matrix.
	 * @param x          X position of the gradient.
	 * @param y          Y position of the gradient.
	 * @param width      Width of the gradient.
	 * @param height     Height of the gradient.
	 * @param startColor The start color of the gradient.
	 * @param endColor   The end color of the gradient.
	 */
	public void drawHorizontalGradient(DrawContext drawContext, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		triangles.triangle(triangles.vec2d(x, y).color(startColor).next(),
				triangles.vec2d(x + width, y).color(startColor).next(),
				triangles.vec2d(x, y + height).color(endColor).next());

		triangles.triangle(triangles.vec2d(x + width, y).color(startColor).next(),
				triangles.vec2d(x + width, y + height).color(endColor).next(),
				triangles.vec2d(x, y + height).color(endColor).next());
	}

	/**
	 * Draws a vertical gradient within a box.
	 *
	 * @param matrix4f   Transformation matrix.
	 * @param size       Size and position of the gradient.
	 * @param startColor The start color of the gradient.
	 * @param endColor   The end color of the gradient.
	 */
	public void drawVerticalGradient(DrawContext drawContext, Rectangle size, Color startColor, Color endColor) {
		drawVerticalGradient(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), startColor,
				endColor);
	}

	/**
	 * Draws a vertical gradient within a box.
	 *
	 * @param matrix4f   Transformation matrix.
	 * @param x          X position of the gradient.
	 * @param y          Y position of the gradient.
	 * @param width      Width of the gradient.
	 * @param height     Height of the gradient.
	 * @param startColor The start color of the gradient.
	 * @param endColor   The end color of the gradient.
	 */
	public void drawVerticalGradient(DrawContext drawContext, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		triangles.triangle(triangles.vec2d(x, y).color(startColor).next(),
				triangles.vec2d(x + width, y).color(endColor).next(),
				triangles.vec2d(x, y + height).color(startColor).next());

		triangles.triangle(triangles.vec2d(x + width, y).color(endColor).next(),
				triangles.vec2d(x + width, y + height).color(endColor).next(),
				triangles.vec2d(x, y + height).color(startColor).next());
	}

	/**
	 * Draws an item in a certain area.
	 *
	 * @param drawContext Draw context.
	 * @param stack       ItemStack to draw.
	 * @param x           X position to draw the item.
	 * @param y           Y position to draw the item.
	 */
	public void drawItem(DrawContext drawContext, ItemStack stack, float x, float y) {
		drawContext.drawItem(stack, (int) x, (int) y);
	}

	/**
	 * Draws a string at a certain position.
	 *
	 * @param drawContext Draw context.
	 * @param text        Text to draw on the screen.
	 * @param x           X position to draw the string.
	 * @param y           Y position to draw the string.
	 * @param color       Color to draw the string.
	 */
	public void drawString(DrawContext drawContext, String text, float x, float y, Color color) {
//		AobaClient aoba = Aoba.getInstance();
//		Matrix3x2fStack matrixStack = drawContext.getMatrices();
//		matrixStack.pushMatrix();
//		matrixStack.scale(2.0f, 2.0f);
//		matrixStack.translate(-x / 2, -y / 2);
//		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
//		matrixStack.popMatrix();
	}

	/**
	 * Draws a string at a certain position.
	 *
	 * @param drawContext Draw context.
	 * @param text        Text to draw on the screen.
	 * @param x           X position to draw the string.
	 * @param y           Y position to draw the string.
	 * @param color       Color (as int) to draw the string.
	 */
	public void drawString(DrawContext drawContext, String text, float x, float y, int color) {
//		AobaClient aoba = Aoba.getInstance();
//		Matrix3x2fStack matrixStack = drawContext.getMatrices();
//		matrixStack.pushMatrix();
//		matrixStack.scale(2.0f, 2.0f);
//		matrixStack.translate(-x / 2, -y / 2);
//		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
//		matrixStack.popMatrix();
	}

	/**
	 * Draws a string at a certain position with a scale.
	 *
	 * @param drawContext Draw context.
	 * @param text        Text to draw on the screen.
	 * @param x           X position to draw the string.
	 * @param y           Y position to draw the string.
	 * @param color       Color to draw the string.
	 * @param scale       Scale to draw the string.
	 */
	public void drawStringWithScale(DrawContext drawContext, String text, float x, float y, Color color, float scale) {
//		AobaClient aoba = Aoba.getInstance();
//		Matrix3x2fStack matrixStack = drawContext.getMatrices();
//		matrixStack.pushMatrix();
//		matrixStack.scale(scale, scale);
//		if (scale > 1.0f) {
//			matrixStack.translate(-x / scale, -y / scale);
//		} else {
//			matrixStack.translate((x / scale) - x, (y * scale) - y);
//		}
//		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
//		matrixStack.popMatrix();
	}

	/**
	 * Draws a string at a certain position with a scale.
	 *
	 * @param drawContext Draw context.
	 * @param text        Text to draw on the screen.
	 * @param x           X position to draw the string.
	 * @param y           Y position to draw the string.
	 * @param color       Color (as int) to draw the string.
	 * @param scale       Scale to draw the string.
	 */
	public void drawStringWithScale(DrawContext drawContext, String text, float x, float y, int color, float scale) {
//		AobaClient aoba = Aoba.getInstance();
//		Matrix3x2fStack matrixStack = drawContext.getMatrices();
//		matrixStack.pushMatrix();
//		matrixStack.scale(scale, scale);
//		if (scale > 1.0f) {
//			matrixStack.translate(-x / scale, -y / scale);
//		} else {
//			matrixStack.translate((x / scale) - x, (y * scale) - y);
//		}
//		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
//		matrixStack.popMatrix();
	}

	/**
	 * Uses a buffer builder to build a filled arc around a position and radius.
	 *
	 * @param bufferBuilder Buffer builder to build vertices with.
	 * @param matrix        Transformation matrix.
	 * @param x             X position to draw arc.
	 * @param y             Y position to draw arc.
	 * @param radius        Radius to draw arc.
	 * @param startAngle    Starting angle of the arc.
	 * @param sweepAngle    Sweep angle of the arc.
	 */
	private static void buildFilledArc(VertexConsumer bufferBuilder, Matrix4f matrix, float x, float y, float radius,
			float startAngle, float sweepAngle, Color color) {
		double roundedInterval = (sweepAngle / radius);

		int colorInt = color.getColorAsInt();

		for (int i = 0; i < radius; i++) {
			double angle = Math.toRadians(startAngle + (i * roundedInterval));
			double angle2 = Math.toRadians(startAngle + ((i + 1) * roundedInterval));
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float) (Math.sin(angle) * radius);
			float radiusX2 = (float) (Math.cos(angle2) * radius);
			float radiusY2 = (float) (Math.sin(angle2) * radius);

			bufferBuilder.vertex(matrix, x, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix, x + radiusX1, y + radiusY1, 0).color(colorInt);
			bufferBuilder.vertex(matrix, x + radiusX2, y + radiusY2, 0).color(colorInt);
		}
	}

	/**
	 * Uses a buffer builder to build an arc around a position and radius.
	 *
	 * @param bufferBuilder Buffer builder to build vertices with.
	 * @param matrix        Transformation matrix.
	 * @param x             X position to draw arc.
	 * @param y             Y position to draw arc.
	 * @param radius        Radius to draw arc.
	 * @param startAngle    Starting angle of the arc.
	 * @param sweepAngle    Sweep angle of the arc.
	 */
	private static void buildArc(VertexConsumer bufferBuilder, Matrix4f matrix, float x, float y, float radius,
			float startAngle, float sweepAngle, Color color) {
		float roundedInterval = (sweepAngle / radius);

		int colorInt = color.getColorAsInt();
		for (int i = 0; i < radius; i++) {
			double angle = Math.toRadians(startAngle + (i * roundedInterval));
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float) Math.sin(angle) * radius;

			bufferBuilder.vertex(matrix, x + radiusX1, y + radiusY1, 0).color(colorInt);
		}
	}

	/**
	 * Wrapper for Minecraft's font renderer getWidth
	 *
	 * @param text Text to get width of.
	 * @return Width of text in pixels.
	 */
	public static int getStringWidth(String text) {
		TextRenderer textRenderer = Aoba.getInstance().fontManager.GetRenderer();
		return textRenderer.getWidth(text);
	}
}
