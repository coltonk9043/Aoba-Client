/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
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
import net.minecraft.util.math.Vec3d;

public class Render2D {
	public static Vec3d center;

	public static MinecraftClient mc = MinecraftClient.getInstance();

	public static void updateScreenCenter() {
		Vector3f pos = new Vector3f(0, 0, 1);

		if (mc.options.getBobView().getValue()) {
			MatrixStack bobViewMatrices = new MatrixStack();

			bobView(bobViewMatrices);
			pos.mulPosition(bobViewMatrices.peek().getPositionMatrix().invert());
		}

		center = new Vec3d(pos.x, -pos.y, pos.z)
				.rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
				.rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
				.add(mc.gameRenderer.getCamera().getPos());
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
	public static void drawTexturedQuad(DrawContext drawContext, Identifier texture, Rectangle size, Color color) {
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
	public static void drawTexturedQuad(DrawContext drawContext, Identifier texture, float x1, float y1, float width,
			float height, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		int colorInt = color.getColorAsInt();

		float x2 = x1 + width;
		float y2 = y1 + height;

		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider
					.getBuffer(RenderLayers.TEXTURES_QUADS_GUI.apply(texture));
			bufferBuilder.vertex(matrix4f, x1, y1, 0).color(colorInt).texture(0, 0);
			bufferBuilder.vertex(matrix4f, x1, y2, 0).color(colorInt).texture(0, 1);
			bufferBuilder.vertex(matrix4f, x2, y2, 0).color(colorInt).texture(1, 1);
			bufferBuilder.vertex(matrix4f, x2, y1, 0).color(colorInt).texture(1, 0);
		});
	}

	/**
	 * Draws a box on the screen.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size and position of the box to draw.
	 * @param color    Color of the box.
	 */
	public static void drawBox(DrawContext drawContext, Rectangle size, Color color) {
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
	public static void drawBox(DrawContext drawContext, float x, float y, float width, float height, Color color) {
		int colorInt = color.getColorAsInt();

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.QUADS_GUI);
			bufferBuilder.vertex(matrix4f, x, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x, y + height, 0).color(colorInt);
		});

	}

	/**
	 * Draws a filled rounded box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size and position of the rounded box.
	 * @param radius   Radius of the box corners.
	 * @param color    Color of the box.
	 */
	public static void drawRoundedBox(DrawContext drawContext, Rectangle size, float radius, Color color) {
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
	public static void drawRoundedBox(DrawContext drawContext, float x, float y, float width, float height,
			float radius, Color color) {
		int colorInt = color.getColorAsInt();

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.TRIS_GUI);
			buildFilledArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, color);
			buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f, color);
			buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f,
					color);
			buildFilledArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f, color);

			// |---
			bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);

			// ---|
			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);

			// _||
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);

			// |||
			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);

			/// __|
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);

			// |__
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(colorInt);

			// |||
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(colorInt);

			/// ||-
			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);

			/// |-/
			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);

			/// /_|
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(colorInt);
		});
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
	public static void drawCircle(DrawContext drawContext, float x, float y, float radius, Color color) {
		int colorInt = color.getColorAsInt();

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.TRIS_GUI);
			double roundedInterval = (360.0f / 30.0f);

			for (int i = 0; i < 30; i++) {
				double angle = Math.toRadians(0 + (i * roundedInterval));
				double angle2 = Math.toRadians(0 + ((i + 1) * roundedInterval));
				float radiusX1 = (float) (Math.cos(angle) * radius);
				float radiusY1 = (float) Math.sin(angle) * radius;
				float radiusX2 = (float) Math.cos(angle2) * radius;
				float radiusY2 = (float) Math.sin(angle2) * radius;

				bufferBuilder.vertex(matrix4f, x, y, 0).color(colorInt);
				bufferBuilder.vertex(matrix4f, x + radiusX1, y + radiusY1, 0).color(colorInt);
				bufferBuilder.vertex(matrix4f, x + radiusX2, y + radiusY2, 0).color(colorInt);
			}
		});
	}

	/**
	 * Draws blurred rounded box onto the screen.
	 *
	 * @param matrix4f Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param width    Width of the box.
	 * @param height   Height of the box.
	 * @param radius   Radius of the corners of the box.
	 * @param color    Color of the box.
	 */
	public static void drawTranslucentBlurredRoundedBox(DrawContext drawContext, float x, float y, float width,
			float height, float radius, Color color) {
		for (int i = 0; i < 5; i++) {
			float r = color.getRed();
			float g = color.getGreen();
			float b = color.getBlue();
			float alpha = color.getAlpha() * (1.0f / (i + 1)); // Adjust alpha for each blur layer

			Color newColor = new Color(r, g, b, alpha);
			drawRoundedBox(drawContext, x - i, y - i, width + 2 * i, height + 2 * i, radius + i, newColor);
		}

		// Draw the main rounded box
		drawRoundedBox(drawContext, x, y, width, height, radius, color);
	}

	/**
	 * Draws a filled AND outlined box.
	 *
	 * @param matrix4f        Transformation matrix
	 * @param size            Size and position to draw the outlined box.
	 * @param outlineColor    Color of the outline of the box.
	 * @param backgroundColor Color of the fill.
	 */
	public static void drawOutlinedBox(DrawContext drawContext, Rectangle size, Color outlineColor,
			Color backgroundColor) {
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
	public static void drawOutlinedBox(DrawContext drawContext, float x, float y, float width, float height,
			Color outlineColor, Color backgroundColor) {

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		int backgroundColorInt = backgroundColor.getColorAsInt();
		int outlineColorInt = outlineColor.getColorAsInt();

		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.QUADS_GUI);
			bufferBuilder.vertex(matrix4f, x, y, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x, y + height, 0).color(backgroundColorInt);
		});

		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.LINES_GUI);
			bufferBuilder.vertex(matrix4f, x, y, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x, y + height, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x, y, 0).color(outlineColorInt);
		});
		GL11.glDisable(GL11.GL_LINE_SMOOTH);

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
	public static void drawBoxOutline(DrawContext drawContext, Rectangle size, Color color) {
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
	public static void drawBoxOutline(DrawContext drawContext, float x, float y, float width, float height,
			Color color) {
		int colorInt = color.getColorAsInt();

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.LINES_GUI);
			bufferBuilder.vertex(matrix4f, x, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x, y + height, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x, y, 0).color(colorInt);

		});
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	/**
	 * Draws the outline of a rounded box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size of the rounded box.
	 * @param radius   Corner radius of the box outline.
	 * @param color    Color of the outline of the box.
	 */
	public static void drawRoundedBoxOutline(DrawContext drawContext, Rectangle size, float radius, Color color) {
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
	public static void drawOutlinedRoundedBox(DrawContext drawContext, float x, float y, float width, float height,
			float radius, Color outlineColor, Color backgroundColor) {

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		int backgroundColorInt = backgroundColor.getColorAsInt();
		int outlineColorInt = outlineColor.getColorAsInt();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.TRIS_GUI);

			//
			buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f,
					backgroundColor);
			buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f,
					backgroundColor);
			buildFilledArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f,
					backgroundColor);
			buildFilledArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, backgroundColor);

			// |---
			bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);

			// ---|

			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);

			// _||
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);

			// |||
			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);

			/// __|
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);

			// |__
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(backgroundColorInt);

			// |||
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(backgroundColorInt);

			/// ||-
			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);

			/// |-/
			bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);

			/// /_|
			bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).color(backgroundColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).color(backgroundColorInt);

			bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.LINES_GUI);
			// Top Left Arc and Top
			buildArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, outlineColor);
			bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(outlineColorInt);

			// Top Right Arc and Right
			buildArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f, outlineColor);
			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(outlineColorInt);

			// Bottom Right
			buildArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f,
					outlineColor);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(outlineColorInt);

			// Bottom Left
			buildArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f, outlineColor);
			bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(outlineColorInt);
			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(outlineColorInt);
		});
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
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
	public static void drawRoundedBoxOutline(DrawContext drawContext, float x, float y, float width, float height,
			float radius, Color color) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		int colorInt = color.getColorAsInt();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.LINES_GUI);
			// Top Left Arc and Top
			buildArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f, color);
			bufferBuilder.vertex(matrix4f, x + radius, y, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).color(colorInt);

			// Top Right Arc and Right
			buildArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f, color);
			bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).color(colorInt);

			// Bottom Right
			buildArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f, color);
			bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).color(colorInt);

			// Bottom Left
			buildArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f, color);
			bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x, y + radius, 0).color(colorInt);
		});
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
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
	public static void drawLine(DrawContext drawContext, float x1, float y1, float x2, float y2, Color color) {
		int colorInt = color.getColorAsInt();

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.LINES_GUI);
			bufferBuilder.vertex(matrix4f, x1, y1, 0).color(colorInt);
			bufferBuilder.vertex(matrix4f, x2, y2, 0).color(colorInt);
		});
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
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
	public static void drawHorizontalGradient(DrawContext drawContext, Rectangle size, Color startColor,
			Color endColor) {
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
	public static void drawHorizontalGradient(DrawContext drawContext, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		int startColorInt = startColor.getColorAsInt();
		int endColorInt = endColor.getColorAsInt();

		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.QUADS_GUI);
			bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(endColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endColorInt);
			bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(startColorInt);
		});
	}

	/**
	 * Draws a vertical gradient within a box.
	 *
	 * @param matrix4f   Transformation matrix.
	 * @param size       Size and position of the gradient.
	 * @param startColor The start color of the gradient.
	 * @param endColor   The end color of the gradient.
	 */
	public static void drawVerticalGradient(DrawContext drawContext, Rectangle size, Color startColor, Color endColor) {
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
	public static void drawVerticalGradient(DrawContext drawContext, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		int startColorInt = startColor.getColorAsInt();
		int endColorInt = endColor.getColorAsInt();

		drawContext.draw(vertexConsumerProvider -> {
			VertexConsumer bufferBuilder = vertexConsumerProvider.getBuffer(RenderLayers.QUADS_GUI);
			bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(startColorInt);
			bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endColorInt);
			bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(endColorInt);
		});
	}

	/**
	 * Draws an item in a certain area.
	 *
	 * @param drawContext Draw context.
	 * @param stack       ItemStack to draw.
	 * @param x           X position to draw the item.
	 * @param y           Y position to draw the item.
	 */
	public static void drawItem(DrawContext drawContext, ItemStack stack, float x, float y) {
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
	public static void drawString(DrawContext drawContext, String text, float x, float y, Color color) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(2.0f, 2.0f, 1.0f);
		matrixStack.translate(-x / 2, -y / 2, 0.0f);
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
		matrixStack.pop();
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
	public static void drawString(DrawContext drawContext, String text, float x, float y, int color) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(2.0f, 2.0f, 1.0f);
		matrixStack.translate(-x / 2, -y / 2, 0.0f);
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
		matrixStack.pop();
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
	public static void drawStringWithScale(DrawContext drawContext, String text, float x, float y, Color color,
			float scale) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(scale, scale, 1.0f);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale, 0.0f);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y, 0.0f);
		}
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
		matrixStack.pop();
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
	public static void drawStringWithScale(DrawContext drawContext, String text, float x, float y, int color,
			float scale) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(scale, scale, 1.0f);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale, 0.0f);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y, 0.0f);
		}
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
		matrixStack.pop();
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
