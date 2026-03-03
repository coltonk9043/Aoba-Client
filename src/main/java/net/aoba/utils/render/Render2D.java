/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.render;

import static net.aoba.AobaClient.MC;

import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class Render2D {
	public static Vec3 center;

	public static Minecraft mc = Minecraft.getInstance();

	private static CachedOrthoProjectionMatrixBuffer guiProjectionBuffer;

	/**
	 * Sets up the orthographic projection for 2D GUI rendering.
	 * Must be called before any 2D draw calls that use endBatch().
	 */
	public static void setup() {
		if (guiProjectionBuffer == null) {
			guiProjectionBuffer = new CachedOrthoProjectionMatrixBuffer("aoba_gui", 1000.0f, 11000.0f, true);
		}

		float scaledWidth = (float) MC.getWindow().getGuiScaledWidth();
		float scaledHeight = (float) MC.getWindow().getGuiScaledHeight();

		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(
				guiProjectionBuffer.getBuffer(scaledWidth, scaledHeight),
				ProjectionType.ORTHOGRAPHIC);

		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix();
		modelViewStack.identity();
		modelViewStack.translate(0.0f, 0.0f, -11000.0f);
	}

	/**
	 * Restores the previous projection state after 2D GUI rendering.
	 */
	public static void end() {
		RenderSystem.getModelViewStack().popMatrix();
		RenderSystem.restoreProjectionMatrix();
	}

	/**
	 * Converts a Matrix3x2fStack to a Matrix4f for vertex rendering.
	 * This is needed because our render pipelines use POSITION_COLOR format
	 * which expects 3D coordinates (x, y, z).
	 */
	public static Matrix4f getAsMatrix(GuiGraphics drawContext) {
		Matrix3x2fStack stack = drawContext.pose();
		Matrix4f matrix = new Matrix4f();
		// Convert 2D matrix to 4D matrix (embed in XY plane at z=0)
		matrix.m00(stack.m00());
		matrix.m01(stack.m01());
		matrix.m02(0);
		matrix.m03(0);
		matrix.m10(stack.m10());
		matrix.m11(stack.m11());
		matrix.m12(0);
		matrix.m13(0);
		matrix.m20(0);
		matrix.m21(0);
		matrix.m22(1);
		matrix.m23(0);
		matrix.m30(stack.m20()); // translation X
		matrix.m31(stack.m21()); // translation Y
		matrix.m32(0);
		matrix.m33(1);
		return matrix;
	}

	public static void updateScreenCenter() {
		Vector3f pos = new Vector3f(0, 0, 1);

		if (mc.options.bobView().get()) {
			PoseStack bobViewMatrices = new PoseStack();

			bobView(bobViewMatrices);
			pos.mulPosition(bobViewMatrices.last().pose().invert());
		}

		center = new Vec3(pos.x, -pos.y, pos.z)
				.xRot(-(float) Math.toRadians(mc.gameRenderer.getMainCamera().xRot()))
				.yRot(-(float) Math.toRadians(mc.gameRenderer.getMainCamera().yRot()))
				.add(mc.gameRenderer.getMainCamera().position());
	}

	private static void bobView(PoseStack matrices) {
		Entity cameraEntity = Minecraft.getInstance().getCameraEntity();

		if (cameraEntity instanceof AbstractClientPlayer abstractClientPlayerEntity) {
			float tickDelta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);

			float speed = abstractClientPlayerEntity.walkAnimation.speed(tickDelta);
			float position = abstractClientPlayerEntity.walkAnimation.position(tickDelta);

			matrices.translate(Mth.sin(position * (float) Math.PI) * speed * 0.5F,
					-Math.abs(Mth.cos(position * (float) Math.PI) * speed), 0.0F);
			matrices.mulPose(Axis.ZP.rotationDegrees(Mth.sin(position * (float) Math.PI) * speed * 3f));
			matrices.mulPose(Axis.XP
					.rotationDegrees(Math.abs(Mth.cos(position * (float) Math.PI - 0.2f) * speed) * 5f));
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
	public static void drawTexturedQuad(GuiGraphics drawContext, Identifier texture, Rectangle size, Color color) {
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
	public static void drawTexturedQuad(GuiGraphics drawContext, Identifier texture, float x1, float y1, float width,
			float height, Color color) {
		Matrix4f matrix = getAsMatrix(drawContext);
		int colorInt = color.getColorAsInt();

		float x2 = x1 + width;
		float y2 = y1 + height;

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.TEXTURES_TRIS_GUI.apply(texture);
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);
		// Triangle 1: TL, TR, BR
		bufferBuilder.addVertex(matrix, x1, y1, 0).setColor(colorInt).setUv(0, 0);
		bufferBuilder.addVertex(matrix, x2, y1, 0).setColor(colorInt).setUv(1, 0);
		bufferBuilder.addVertex(matrix, x2, y2, 0).setColor(colorInt).setUv(1, 1);
		// Triangle 2: TL, BR, BL
		bufferBuilder.addVertex(matrix, x1, y1, 0).setColor(colorInt).setUv(0, 0);
		bufferBuilder.addVertex(matrix, x2, y2, 0).setColor(colorInt).setUv(1, 1);
		bufferBuilder.addVertex(matrix, x1, y2, 0).setColor(colorInt).setUv(0, 1);
		bufferSource.endBatch(layer);
	}

	/**
	 * Draws a box on the screen.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size and position of the box to draw.
	 * @param color    Color of the box.
	 */
	public static void drawBox(GuiGraphics drawContext, Rectangle size, Color color) {
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
	public static void drawBox(GuiGraphics drawContext, float x, float y, float width, float height, Color color) {
		int colorInt = color.getColorAsInt();
		Matrix4f matrix = getAsMatrix(drawContext);

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.TRIS_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);
		// Triangle 1: TL, TR, BR
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(colorInt);
		// Triangle 2: TL, BR, BL
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(colorInt);
		bufferSource.endBatch(layer);
	}

	/**
	 * Draws a filled rounded box.
	 *
	 * @param matrix4f Transformation matrix
	 * @param size     Size and position of the rounded box.
	 * @param radius   Radius of the box corners.
	 * @param color    Color of the box.
	 */
	public static void drawRoundedBox(GuiGraphics drawContext, Rectangle size, float radius, Color color) {
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
	public static void drawRoundedBox(GuiGraphics drawContext, float x, float y, float width, float height,
			float radius, Color color) {
		int colorInt = color.getColorAsInt();
		Matrix4f matrix = getAsMatrix(drawContext);

		// Clamp radius to avoid geometry issues
		float r = Math.min(radius, Math.min(width / 2, height / 2));
		if (r < 0) r = 0;

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.TRIS_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);

		// Draw corner arcs (only if radius > 0)
		if (r > 0) {
			buildFilledArc(bufferBuilder, matrix, x + r, y + r, r, 180.0f, 90.0f, color);
			buildFilledArc(bufferBuilder, matrix, x + width - r, y + r, r, 270.0f, 90.0f, color);
			buildFilledArc(bufferBuilder, matrix, x + width - r, y + height - r, r, 0.0f, 90.0f, color);
			buildFilledArc(bufferBuilder, matrix, x + r, y + height - r, r, 90.0f, 90.0f, color);
		}

		// Horizontal band covering the full width (from y+r to y+h-r)
		// Triangle 1: TL, TR, BR
		bufferBuilder.addVertex(matrix, x, y + r, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y + r, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height - r, 0).setColor(colorInt);
		// Triangle 2: TL, BR, BL
		bufferBuilder.addVertex(matrix, x, y + r, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height - r, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x, y + height - r, 0).setColor(colorInt);

		// Top cap (from x+r to x+w-r, y to y+r)
		if (r > 0) {
			// Triangle 1: TL, TR, BR
			bufferBuilder.addVertex(matrix, x + r, y, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + width - r, y, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + width - r, y + r, 0).setColor(colorInt);
			// Triangle 2: TL, BR, BL
			bufferBuilder.addVertex(matrix, x + r, y, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + width - r, y + r, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + r, y + r, 0).setColor(colorInt);

			// Bottom cap (from x+r to x+w-r, y+h-r to y+h)
			// Triangle 1: TL, TR, BR
			bufferBuilder.addVertex(matrix, x + r, y + height - r, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + width - r, y + height - r, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + width - r, y + height, 0).setColor(colorInt);
			// Triangle 2: TL, BR, BL
			bufferBuilder.addVertex(matrix, x + r, y + height - r, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + width - r, y + height, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + r, y + height, 0).setColor(colorInt);
		}

		bufferSource.endBatch(layer);
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
	public static void drawCircle(GuiGraphics drawContext, float x, float y, float radius, Color color) {
		int colorInt = color.getColorAsInt();
		Matrix4f matrix = getAsMatrix(drawContext);

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.TRIS_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);

		double roundedInterval = (360.0f / 30.0f);
		for (int i = 0; i < 30; i++) {
			double angle = Math.toRadians(i * roundedInterval);
			double angle2 = Math.toRadians((i + 1) * roundedInterval);
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float) (Math.sin(angle) * radius);
			float radiusX2 = (float) (Math.cos(angle2) * radius);
			float radiusY2 = (float) (Math.sin(angle2) * radius);

			bufferBuilder.addVertex(matrix, x, y, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + radiusX1, y + radiusY1, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + radiusX2, y + radiusY2, 0).setColor(colorInt);
		}

		bufferSource.endBatch(layer);
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
	public static void drawTranslucentBlurredRoundedBox(GuiGraphics drawContext, float x, float y, float width,
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
	public static void drawOutlinedBox(GuiGraphics drawContext, Rectangle size, Color outlineColor,
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
	public static void drawOutlinedBox(GuiGraphics drawContext, float x, float y, float width, float height,
			Color outlineColor, Color backgroundColor) {
		Matrix4f matrix = getAsMatrix(drawContext);
		int backgroundColorInt = backgroundColor.getColorAsInt();
		int outlineColorInt = outlineColor.getColorAsInt();

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();

		// Draw filled box
		RenderType trisLayer = RenderLayers.TRIS_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(trisLayer);
		// Triangle 1: TL, TR, BR
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(backgroundColorInt);
		bufferBuilder.addVertex(matrix, x + width, y, 0).setColor(backgroundColorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(backgroundColorInt);
		// Triangle 2: TL, BR, BL
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(backgroundColorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(backgroundColorInt);
		bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(backgroundColorInt);
		bufferSource.endBatch(trisLayer);

		// Draw outline (LINE_STRIP: 5 vertices to close the loop)
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		RenderType linesLayer = RenderLayers.LINES_GUI;
		bufferBuilder = bufferSource.getBuffer(linesLayer);
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(outlineColorInt);
		bufferBuilder.addVertex(matrix, x + width, y, 0).setColor(outlineColorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(outlineColorInt);
		bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(outlineColorInt);
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(outlineColorInt);
		bufferSource.endBatch(linesLayer);
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
	public static void drawBoxOutline(GuiGraphics drawContext, Rectangle size, Color color) {
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
	public static void drawBoxOutline(GuiGraphics drawContext, float x, float y, float width, float height,
			Color color) {
		int colorInt = color.getColorAsInt();
		Matrix4f matrix = getAsMatrix(drawContext);

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		RenderType layer = RenderLayers.LINES_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);
		// LINE_STRIP: 5 vertices to close the loop
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(colorInt);
		bufferSource.endBatch(layer);
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
	public static void drawRoundedBoxOutline(GuiGraphics drawContext, Rectangle size, float radius, Color color) {
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
	public static void drawOutlinedRoundedBox(GuiGraphics drawContext, float x, float y, float width, float height,
			float radius, Color outlineColor, Color backgroundColor) {
		// Draw the filled rounded box first
		drawRoundedBox(drawContext, x, y, width, height, radius, backgroundColor);
		// Then draw the outline
		drawRoundedBoxOutline(drawContext, x, y, width, height, radius, outlineColor);
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
	public static void drawRoundedBoxOutline(GuiGraphics drawContext, float x, float y, float width, float height,
			float radius, Color color) {
		Matrix4f matrix = getAsMatrix(drawContext);
		int colorInt = color.getColorAsInt();

		// Clamp radius
		float r = Math.min(radius, Math.min(width / 2, height / 2));
		if (r < 0) r = 0;

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		RenderType layer = RenderLayers.LINES_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);

		if (r > 0) {
			// Top Left Arc
			buildArc(bufferBuilder, matrix, x + r, y + r, r, 180.0f, 90.0f, color);
		}
		// Top edge
		bufferBuilder.addVertex(matrix, x + r, y, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width - r, y, 0).setColor(colorInt);

		if (r > 0) {
			// Top Right Arc
			buildArc(bufferBuilder, matrix, x + width - r, y + r, r, 270.0f, 90.0f, color);
		}
		// Right edge
		bufferBuilder.addVertex(matrix, x + width, y + r, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height - r, 0).setColor(colorInt);

		if (r > 0) {
			// Bottom Right Arc
			buildArc(bufferBuilder, matrix, x + width - r, y + height - r, r, 0.0f, 90.0f, color);
		}
		// Bottom edge
		bufferBuilder.addVertex(matrix, x + width - r, y + height, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x + r, y + height, 0).setColor(colorInt);

		if (r > 0) {
			// Bottom Left Arc
			buildArc(bufferBuilder, matrix, x + r, y + height - r, r, 90.0f, 90.0f, color);
		}
		// Left edge and closing vertex
		bufferBuilder.addVertex(matrix, x, y + height - r, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x, y + r, 0).setColor(colorInt);

		bufferSource.endBatch(layer);
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
	public static void drawLine(GuiGraphics drawContext, float x1, float y1, float x2, float y2, Color color) {
		int colorInt = color.getColorAsInt();
		Matrix4f matrix = getAsMatrix(drawContext);

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		RenderType layer = RenderLayers.LINES_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);
		bufferBuilder.addVertex(matrix, x1, y1, 0).setColor(colorInt);
		bufferBuilder.addVertex(matrix, x2, y2, 0).setColor(colorInt);
		bufferSource.endBatch(layer);
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
	public static void drawHorizontalGradient(GuiGraphics drawContext, Rectangle size, Color startColor,
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
	public static void drawHorizontalGradient(GuiGraphics drawContext, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		int startColorInt = startColor.getColorAsInt();
		int endColorInt = endColor.getColorAsInt();
		Matrix4f matrix = getAsMatrix(drawContext);

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.TRIS_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);
		// Triangle 1: TL, TR, BR
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(startColorInt);
		bufferBuilder.addVertex(matrix, x + width, y, 0).setColor(endColorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(endColorInt);
		// Triangle 2: TL, BR, BL
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(startColorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(endColorInt);
		bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(startColorInt);
		bufferSource.endBatch(layer);
	}

	/**
	 * Draws a vertical gradient within a box.
	 *
	 * @param matrix4f   Transformation matrix.
	 * @param size       Size and position of the gradient.
	 * @param startColor The start color of the gradient.
	 * @param endColor   The end color of the gradient.
	 */
	public static void drawVerticalGradient(GuiGraphics drawContext, Rectangle size, Color startColor, Color endColor) {
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
	public static void drawVerticalGradient(GuiGraphics drawContext, float x, float y, float width, float height,
			Color startColor, Color endColor) {
		int startColorInt = startColor.getColorAsInt();
		int endColorInt = endColor.getColorAsInt();
		Matrix4f matrix = getAsMatrix(drawContext);

		MultiBufferSource.BufferSource bufferSource = MC.renderBuffers().bufferSource();
		RenderType layer = RenderLayers.TRIS_GUI;
		VertexConsumer bufferBuilder = bufferSource.getBuffer(layer);
		// Triangle 1: TL, TR, BR
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(startColorInt);
		bufferBuilder.addVertex(matrix, x + width, y, 0).setColor(startColorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(endColorInt);
		// Triangle 2: TL, BR, BL
		bufferBuilder.addVertex(matrix, x, y, 0).setColor(startColorInt);
		bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(endColorInt);
		bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(endColorInt);
		bufferSource.endBatch(layer);
	}

	/**
	 * Draws an item in a certain area.
	 *
	 * @param drawContext Draw context.
	 * @param stack       ItemStack to draw.
	 * @param x           X position to draw the item.
	 * @param y           Y position to draw the item.
	 */
	public static void drawItem(GuiGraphics drawContext, ItemStack stack, float x, float y) {
		drawContext.renderItem(stack, (int) x, (int) y);
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
	public static void drawString(GuiGraphics drawContext, String text, float x, float y, Color color) {
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.pose();
		matrixStack.pushMatrix();
		matrixStack.scale(2.0f, 2.0f);
		matrixStack.translate(-x / 2, -y / 2);
		drawContext.drawString(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
		matrixStack.popMatrix();
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
	public static void drawString(GuiGraphics drawContext, String text, float x, float y, int color) {
		if ((color & 0xFF000000) == 0) {
			color |= 0xFF000000;
		}
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.pose();
		matrixStack.pushMatrix();
		matrixStack.scale(2.0f, 2.0f);
		matrixStack.translate(-x / 2, -y / 2);
		drawContext.drawString(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
		matrixStack.popMatrix();
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
	public static void drawStringWithScale(GuiGraphics drawContext, String text, float x, float y, Color color,
			float scale) {
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.pose();
		matrixStack.pushMatrix();
		matrixStack.scale(scale, scale);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y);
		}
		drawContext.drawString(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color.getColorAsInt(), false);
		matrixStack.popMatrix();
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
	public static void drawStringWithScale(GuiGraphics drawContext, String text, float x, float y, int color,
			float scale) {
		if ((color & 0xFF000000) == 0) {
			color |= 0xFF000000;
		}
		AobaClient aoba = Aoba.getInstance();
		Matrix3x2fStack matrixStack = drawContext.pose();
		matrixStack.pushMatrix();
		matrixStack.scale(scale, scale);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y);
		}
		drawContext.drawString(aoba.fontManager.GetRenderer(), text, (int) x, (int) y, color, false);
		matrixStack.popMatrix();
	}

	/**
	 * Uses a buffer builder to build a filled arc around a position and radius.
	 */
	private static void buildFilledArc(VertexConsumer bufferBuilder, Matrix4f matrix, float x, float y, float radius,
			float startAngle, float sweepAngle, Color color) {
		int segments = Math.max((int) Math.ceil(radius), 1);
		float interval = sweepAngle / segments;
		int colorInt = color.getColorAsInt();

		for (int i = 0; i < segments; i++) {
			double angle = Math.toRadians(startAngle + (i * interval));
			double angle2 = Math.toRadians(startAngle + ((i + 1) * interval));
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float) (Math.sin(angle) * radius);
			float radiusX2 = (float) (Math.cos(angle2) * radius);
			float radiusY2 = (float) (Math.sin(angle2) * radius);

			bufferBuilder.addVertex(matrix, x, y, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + radiusX1, y + radiusY1, 0).setColor(colorInt);
			bufferBuilder.addVertex(matrix, x + radiusX2, y + radiusY2, 0).setColor(colorInt);
		}
	}

	/**
	 * Uses a buffer builder to build an arc outline around a position and radius.
	 * Includes both start and end points for proper LINE_STRIP connectivity.
	 */
	private static void buildArc(VertexConsumer bufferBuilder, Matrix4f matrix, float x, float y, float radius,
			float startAngle, float sweepAngle, Color color) {
		int segments = Math.max((int) Math.ceil(radius), 1);
		float interval = sweepAngle / segments;
		int colorInt = color.getColorAsInt();

		// Include endpoint (i <= segments) for smooth connection to straight edges
		for (int i = 0; i <= segments; i++) {
			double angle = Math.toRadians(startAngle + (i * interval));
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float) (Math.sin(angle) * radius);

			bufferBuilder.addVertex(matrix, x + radiusX1, y + radiusY1, 0).setColor(colorInt);
		}
	}

	/**
	 * Wrapper for Minecraft's font renderer getWidth
	 *
	 * @param text Text to get width of.
	 * @return Width of text in pixels.
	 */
	public static int getStringWidth(String text) {
		Font textRenderer = Aoba.getInstance().fontManager.GetRenderer();
		return textRenderer.width(text);
	}
}
