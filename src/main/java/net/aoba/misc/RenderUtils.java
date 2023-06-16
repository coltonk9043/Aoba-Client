/*
* Aoba Hacked Client
* Copyright (C) 2019-2023 coltonk9043
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

import net.aoba.gui.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.systems.RenderSystem;

public class RenderUtils {
	public void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, float r, float g, float b,
			float alpha) {
		
		RenderSystem.setShaderColor(r, g, b, alpha);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, x, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix, x, y + height, 0).next();

		tessellator.draw();
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color, float alpha) {

		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix, x, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix, x, y + height, 0).next();

		tessellator.draw();
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawOutlinedBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color,
			float alpha) {

		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, x, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix, x, y + height, 0).next();

		tessellator.draw();
		
		RenderSystem.setShaderColor(0, 0, 0, alpha);
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, x, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix, x, y + height, 0).next();
		bufferBuilder.vertex(matrix, x, y, 0).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawOutline(MatrixStack matrixStack, int x, int y, int width, int height) {

		RenderSystem.setShaderColor(0, 0, 0, 1);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, x, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y, 0).next();
		bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
		bufferBuilder.vertex(matrix, x, y + height, 0).next();
		bufferBuilder.vertex(matrix, x, y, 0).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void draw3DBox(MatrixStack matrixStack, Box box, Color color, float alpha) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), 1.0f);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();

		tessellator.draw();
		
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();

		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();

		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).next();
		bufferBuilder.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).next();
		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawLine3D(MatrixStack matrixStack, Vec3d pos, Vec3d pos2, Color color) {
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), 1.0f);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

		bufferBuilder.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).next();
		bufferBuilder.vertex(matrix, (float) pos2.x, (float) pos2.y, (float) pos2.z).next();

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawString(DrawContext drawContext, String text, float x, float y, Color color) {
		MinecraftClient mc = MinecraftClient.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(2.0f, 2.0f, 1.0f);
		matrixStack.translate(-x / 2, -y / 2, 0.0f);
		drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color.getColorAsInt(), false);
		matrixStack.pop();
	}

	public void drawString(DrawContext drawContext, String text, float x, float y, int color) {
		MinecraftClient mc = MinecraftClient.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(2.0f, 2.0f, 1.0f);
		matrixStack.translate(-x / 2, -y / 2, 0.0f);
		drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color, false);
		matrixStack.pop();
	}

	public void drawStringWithScale(DrawContext drawContext, String text, float x, float y, Color color, float scale) {
		MinecraftClient mc = MinecraftClient.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(scale, scale, 1.0f);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale, 0.0f);
		} else {
			matrixStack.translate((x / scale) - x, (y * scale) - y, 0.0f);
		}
		drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color.getColorAsInt(), false);
		matrixStack.pop();
	}

	public void drawStringWithScale(DrawContext drawContext, String text, float x, float y, int color, float scale) {
		MinecraftClient mc = MinecraftClient.getInstance();
		MatrixStack matrixStack = drawContext.getMatrices();
		matrixStack.push();
		matrixStack.scale(scale, scale, 1.0f);
		if (scale > 1.0f) {
			matrixStack.translate(-x / scale, -y / scale, 0.0f);
		} else {
			matrixStack.translate(x / scale, y * scale, 0.0f);
		}
		drawContext.drawText(mc.textRenderer, text, (int)x, (int)y, color, false);
		matrixStack.pop();
	}

	public static void applyRenderOffset(MatrixStack matrixStack) {
		Vec3d camPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos();
		matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
	}

	public static void applyRegionalRenderOffset(MatrixStack matrixStack) {
		Vec3d camPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos();
		BlockPos camBlockPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getBlockPos();

		int regionX = (camBlockPos.getX() >> 9) * 512;
		int regionZ = (camBlockPos.getZ() >> 9) * 512;

		matrixStack.translate(regionX - camPos.x, -camPos.y, regionZ - camPos.z);
	}
}
