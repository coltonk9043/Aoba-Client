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

	private final NewRender2D newRenderer;
	private final RenderManager renderManager;

	public Render2D() {
		this.renderManager = RenderManager.getInstance();
		this.newRenderer = renderManager.get2D();
	}

	public void begin() {
		renderManager.begin2D();
	}

	public void end() {
		renderManager.end2D();
	}

	public void render(DrawContext context) {
		newRenderer.render(context);
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
	 * @param drawContext Transformation matrix.
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
	 * @param drawContext Transformation matrix
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
		newRenderer.drawBox(x, y, width, height, color);
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
		newRenderer.drawRoundedBox(x, y, width, height, radius, color);
	}

	/**
	 * Draws a filled circle.
	 *
	 * @param drawContext Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param radius   Radius of the circle.
	 * @param color    Color of the box.
	 */
	public void drawCircle(DrawContext drawContext, float x, float y, float radius, Color color) {
		newRenderer.drawCircle(x, y, radius, color);
	}

	/**
	 * Draws a filled AND outlined box.
	 *
	 * @param drawContext        Transformation matrix
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
	 * @param drawContext        Transformation matrix
	 * @param x               X position of the box.
	 * @param y               Y position of the box.
	 * @param width           Width of the box.
	 * @param height          Height of the box.
	 * @param outlineColor    Color of the outline of the box.
	 * @param backgroundColor Color of the fill.
	 */
	public void drawOutlinedBox(DrawContext drawContext, float x, float y, float width, float height,
			Color outlineColor, Color backgroundColor) {
		newRenderer.drawOutlinedBox(x, y, width, height, outlineColor, backgroundColor);
	}

	/**
	 ** OUTLINES
	 **/

	/**
	 * Draws the outline of a box.
	 *
	 * @param drawContext Transformation matrix
	 * @param size     Size and position of the box.
	 * @param color    Color of the box.
	 */
	public void drawBoxOutline(DrawContext drawContext, Rectangle size, Color color) {
		drawBoxOutline(drawContext, size.getX(), size.getY(), size.getWidth(), size.getHeight(), color);
	}

	/**
	 * Draws the outline of a box.
	 *
	 * @param drawContext Transformation matrix
	 * @param x        X position of the box.
	 * @param y        Y position of the box.
	 * @param width    Width of the box.
	 * @param height   Height of the box.
	 * @param color    Color of the box.
	 */
	public void drawBoxOutline(DrawContext drawContext, float x, float y, float width, float height, Color color) {
		newRenderer.drawBoxOutline(x, y, width, height, color);
	}

	/**
	 * Draws the outline of a rounded box.
	 *
	 * @param drawContext Transformation matrix
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
	 * @param drawContext        Transformation matrix
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
		newRenderer.drawLine(x1, y1, x2, y2, color);
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
		newRenderer.drawHorizontalGradient(x, y, width, height, startColor, endColor);
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
		newRenderer.drawVerticalGradient(x, y, width, height, startColor, endColor);
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
	 * Helper: Adds a filled arc (pie slice) to the triangle mesh.
	 */
	private void buildFilledArc(float cx, float cy, float radius, float startAngle, float arcAngle, Color color) {
		int segments = Math.max(8, (int) (radius * arcAngle / 24));
		float angleStep = arcAngle / segments;
		for (int i = 0; i < segments; i++) {
			float a0 = (float) Math.toRadians(startAngle + i * angleStep);
			float a1 = (float) Math.toRadians(startAngle + (i + 1) * angleStep);
			float x0 = cx + (float) Math.cos(a0) * radius;
			float y0 = cy + (float) Math.sin(a0) * radius;
			float x1 = cx + (float) Math.cos(a1) * radius;
			float y1 = cy + (float) Math.sin(a1) * radius;
			newRenderer.drawTriangle(cx, cy, color, x0, y0, color, x1, y1, color);
		}
	}

	/**
	 * Helper: Adds an arc outline to the line mesh.
	 */
	private void buildArc(float cx, float cy, float radius, float startAngle, float arcAngle, Color color) {
		int segments = Math.max(8, (int) (radius * arcAngle / 24));
		float angleStep = arcAngle / segments;
		float prevX = cx + (float) Math.cos(Math.toRadians(startAngle)) * radius;
		float prevY = cy + (float) Math.sin(Math.toRadians(startAngle)) * radius;
		for (int i = 1; i <= segments; i++) {
			float a = (float) Math.toRadians(startAngle + i * angleStep);
			float x = cx + (float) Math.cos(a) * radius;
			float y = cy + (float) Math.sin(a) * radius;
			newRenderer.drawLine(prevX, prevY, x, y, color);
			prevX = x;
			prevY = y;
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
