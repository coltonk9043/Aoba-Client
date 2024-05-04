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
package net.aoba.misc;

import net.aoba.Aoba;
import net.aoba.AobaClient;
import net.aoba.gui.colors.Color;
import net.aoba.gui.colors.Colors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.systems.RenderSystem;

public class RenderUtils {

	static final float ROUND_QUALITY = 10;

	public static void drawTexturedQuad(Matrix4f matrix4f, Identifier texture, float x1, float y1, float width, float height, Color color) {
		float red = color.getRedFloat();
		float green = color.getGreenFloat(); 
		float blue = color.getBlueFloat(); 
		float alpha = color.getAlphaFloat();
		
		float x2 = x1 + width;
		float y2 = y1 + height;
		
		RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, 0).color(red, green, blue, alpha).texture(0, 0).next();
        bufferBuilder.vertex(matrix4f, x1, y2, 0).color(red, green, blue, alpha).texture(0, 1).next();
        bufferBuilder.vertex(matrix4f, x2, y2, 0).color(red, green, blue, alpha).texture(1, 1).next();
        bufferBuilder.vertex(matrix4f, x2, y1, 0).color(red, green, blue, alpha).texture(1, 0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
	}
	
	public static void drawBox(Matrix4f matrix4f, float x, float y, float width, float height, Color color) {

		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, x, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y + height, 0).next();

		tessellator.draw();
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawCircle(Matrix4f matrix4f, float x, float y, float radius, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION);
		
		double roundedInterval = (360.0f / 30.0f);
		
		for(int i = 0; i < 30; i++) {
			double angle = Math.toRadians(0 + (i * roundedInterval));
			double angle2 = Math.toRadians(0 + ((i + 1) * roundedInterval));
			float radiusX1 = (float)(Math.cos(angle) * radius);
			float radiusY1 = (float)Math.sin(angle) * radius;
			float radiusX2 = (float)Math.cos(angle2) * radius;
			float radiusY2 = (float)Math.sin(angle2) * radius;
			
			bufferBuilder.vertex(matrix4f, x, y, 0).next();
			bufferBuilder.vertex(matrix4f, x + radiusX1, y + radiusY1, 0).next();
			bufferBuilder.vertex(matrix4f, x + radiusX2, y + radiusY2, 0).next();
		}
		
		tessellator.draw();
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawRoundedBox(Matrix4f matrix4f, float x, float y, float width, float height, float radius, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION);
		
		buildFilledArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f);
		buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f);
		buildFilledArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f);
		buildFilledArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f);
		
		// |---
		bufferBuilder.vertex(matrix4f, x + radius, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).next();
		
		// ---|
		bufferBuilder.vertex(matrix4f, x + radius, y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).next();
		
		// _||
		bufferBuilder.vertex(matrix4f, x + width - radius, y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).next();
		
		// |||
		bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).next();
		
		/// __|
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).next();
		
		// |__
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).next();
		
		// |||
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x , y + radius, 0).next();
		
		/// ||-
		bufferBuilder.vertex(matrix4f, x , y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + radius , y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + radius, y + height - radius, 0).next();

		/// |-/
		bufferBuilder.vertex(matrix4f, x + radius , y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius , y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + radius , y + height - radius, 0).next();
		
		/// /_|
		bufferBuilder.vertex(matrix4f, x + radius , y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius , y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius , y + radius, 0).next();
		
		tessellator.draw();
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawRoundedOutline(Matrix4f matrix4f, float x, float y, float width, float height, float radius, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		
		// Top Left Arc and Top
		buildArc(bufferBuilder, matrix4f, x + radius, y + radius, radius, 180.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x + radius, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width - radius, y, 0).next();
		
		// Top Right Arc and Right
		buildArc(bufferBuilder, matrix4f, x + width - radius, y + radius, radius, 270.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x + width, y + radius, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + height - radius, 0).next();
		
		// Bottom Right
		buildArc(bufferBuilder, matrix4f, x + width - radius, y + height - radius, radius, 0.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x + width - radius, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x + radius, y + height, 0).next();
		
		// Bottom Left
		buildArc(bufferBuilder, matrix4f, x + radius, y + height - radius, radius, 90.0f, 90.0f);
		bufferBuilder.vertex(matrix4f, x, y + height - radius, 0).next();
		bufferBuilder.vertex(matrix4f, x, y + radius, 0).next();
		
		tessellator.draw();
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public static void drawOutlinedBox(Matrix4f matrix4f, float x, float y, float width, float height, Color outlineColor, Color backgroundColor) {
		RenderSystem.setShaderColor(backgroundColor.getRedFloat(), backgroundColor.getGreenFloat(), backgroundColor.getBlueFloat(), backgroundColor.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, x, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y + height, 0).next();

		tessellator.draw();
		
		RenderSystem.setShaderColor(outlineColor.getRedFloat(), outlineColor.getGreenFloat(), outlineColor.getBlueFloat(), outlineColor.getAlphaFloat());
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, x, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y, 0).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawOutlinedBox(Matrix4f matrix4f, float x, float y, float width, float height, Color color) {
		drawOutlinedBox(matrix4f, x, y, width, height, Colors.Black, color);
	}

	public static void drawLine(Matrix4f matrix4f, float x1, float y1, float x2, float y2, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, x1, y1, 0).next();
		bufferBuilder.vertex(matrix4f, x2, y2, 0).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawHorizontalGradient(Matrix4f matrix4f, float x, float y, float width, float height, Color startColor, Color endColor) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startColor.getColorAsInt()).next();
        bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(endColor.getColorAsInt()).next();
        bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endColor.getColorAsInt()).next();
        bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(startColor.getColorAsInt()).next();
		
		tessellator.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawVerticalGradient(Matrix4f matrix4f, float x, float y, float width, float height, Color startColor, Color endColor) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(startColor.getColorAsInt()).next();
        bufferBuilder.vertex(matrix4f, x + width, y, 0.0F).color(startColor.getColorAsInt()).next();
        bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0F).color(endColor.getColorAsInt()).next();
        bufferBuilder.vertex(matrix4f, x, y + height, 0.0F).color(endColor.getColorAsInt()).next();
		
		tessellator.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawOutline(Matrix4f matrix4f, float x, float y, float width, float height) {
		RenderSystem.setShaderColor(0, 0, 0, 1);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, x, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y, 0).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void drawOutline(Matrix4f matrix4f, float x, float y, float width, float height, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, x, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y, 0).next();
		bufferBuilder.vertex(matrix4f, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y + height, 0).next();
		bufferBuilder.vertex(matrix4f, x, y, 0).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void draw3DBox(Matrix4f matrix4f, Box box, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), 1.0f);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ).next();

		tessellator.draw();
		
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix4f, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawLine3D(Matrix4f matrix4f, Vec3d pos, Vec3d pos2, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), color.getAlphaFloat());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix4f, (float) pos.x, (float) pos.y, (float) pos.z).next();
		bufferBuilder.vertex(matrix4f, (float) pos2.x, (float) pos2.y, (float) pos2.z).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void drawString(DrawContext drawContext, String text, float x, float y, Color color) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(2.0f, 2.0f, 1.0f);
		matrixStack.translate(-x / 2, -y / 2, 0.0f);
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int)x, (int)y, color.getColorAsInt(), false);
		matrixStack.pop();
	}

	public static void drawString(DrawContext drawContext, String text, float x, float y, int color) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(2.0f, 2.0f, 1.0f);
		matrixStack.translate(-x / 2, -y / 2, 0.0f);
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int)x, (int)y, color, false);
		matrixStack.pop();
	}

	public static void drawStringWithScale(DrawContext drawContext, String text, float x, float y, Color color, float scale) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(scale, scale, 1.0f);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale, 0.0f);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y, 0.0f);
		}
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int)x, (int)y, color.getColorAsInt(), false);
		matrixStack.pop();
	}
	
	public static void drawStringWithScale(DrawContext drawContext, String text, float x, float y, int color, float scale) {
		AobaClient aoba = Aoba.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(scale, scale, 1.0f);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale, 0.0f);
		} else {
			matrixStack.translate(x / scale, y * scale, 0.0f);
		}
		drawContext.drawText(aoba.fontManager.GetRenderer(), text, (int)x, (int)y, color, false);
		matrixStack.pop();
	}

	private static void buildFilledArc(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float radius, float startAngle, float sweepAngle) {
		double roundedInterval = (sweepAngle / ROUND_QUALITY);
		
				for(int i = 0; i < ROUND_QUALITY; i++) {
					double angle = Math.toRadians(startAngle + (i * roundedInterval));
					double angle2 = Math.toRadians(startAngle + ((i + 1) * roundedInterval));
					float radiusX1 = (float)(Math.cos(angle) * radius);
					float radiusY1 = (float)Math.sin(angle) * radius;
					float radiusX2 = (float)Math.cos(angle2) * radius;
					float radiusY2 = (float)Math.sin(angle2) * radius;
					
					bufferBuilder.vertex(matrix, x, y, 0).next();
					bufferBuilder.vertex(matrix, x + radiusX1, y + radiusY1, 0).next();
					bufferBuilder.vertex(matrix, x + radiusX2, y + radiusY2, 0).next();
				}
	}
	
	private static void buildArc(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float radius, float startAngle, float sweepAngle) {
		double roundedInterval = (sweepAngle / ROUND_QUALITY);
		
		for(int i = 0; i < ROUND_QUALITY; i++) {
			double angle = Math.toRadians(startAngle + (i * roundedInterval));
			float radiusX1 = (float) (Math.cos(angle) * radius);
			float radiusY1 = (float)Math.sin(angle) * radius;

			bufferBuilder.vertex(matrix, x + radiusX1, y + radiusY1, 0).next();
		}
	}
	
	/**
	 * Gets the interpolated position of the entity given a tick delta.
	 * @param entity Entity to get position of
	 * @param delta Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3d getEntityPositionInterpolated(Entity entity, float delta) {
		return new Vec3d(MathHelper.lerp(delta, entity.prevX, entity.getX()),
						 MathHelper.lerp(delta, entity.prevY, entity.getY()),
						 MathHelper.lerp(delta, entity.prevZ, entity.getZ()));
	}
	
	/**
	 * Gets the difference between the interpolated position and 
	 * @param entity Entity to get position of
	 * @param delta Tick delta.
	 * @return Vec3d representing the interpolated position of the entity.
	 */
	public static Vec3d getEntityPositionOffsetInterpolated(Entity entity, float delta) {
		Vec3d interpolated = getEntityPositionInterpolated(entity, delta);
		return entity.getPos().subtract(interpolated);
	}
}
