package net.aoba.misc;

import net.aoba.gui.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilder.BuiltBuffer;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;

public class RenderUtils {
	public void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, float r, float g, float b,
			float alpha) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(r, g, b, alpha);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
			bufferBuilder.vertex(matrix, x, y + height, 0).next();
		}
		BuiltBuffer buffer = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color, float alpha) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
			bufferBuilder.vertex(matrix, x, y + height, 0).next();
		}
		BuiltBuffer buffer = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawOutlinedBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color,
			float alpha) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
			bufferBuilder.vertex(matrix, x, y + height, 0).next();
		}
		BuiltBuffer buffer = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer);

		RenderSystem.setShaderColor(0, 0, 0, alpha);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
			bufferBuilder.vertex(matrix, x, y + height, 0).next();
			bufferBuilder.vertex(matrix, x, y, 0).next();
		}
		BuiltBuffer buffer2 = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer2);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void drawOutline(MatrixStack matrixStack, int x, int y, int width, int height) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);

		RenderSystem.setShaderColor(0, 0, 0, 1);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + height, 0).next();
			bufferBuilder.vertex(matrix, x, y + height, 0).next();
			bufferBuilder.vertex(matrix, x, y, 0).next();
		}
		BuiltBuffer buffer = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public void draw3DBox(MatrixStack matrixStack, Box box, Color color, float alpha) {
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), 1.0f);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
		{
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
		}
		BuiltBuffer buffer = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer);

		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
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
		}
		BuiltBuffer buffer2 = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer2);
	}

	public void drawLine3D(MatrixStack matrixStack, Vec3d pos, Vec3d pos2, Color color) {
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), 1.0f);

		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).next();
			bufferBuilder.vertex(matrix, (float) pos2.x, (float) pos2.y, (float) pos2.z).next();
		}
		BuiltBuffer buffer = bufferBuilder.end();
		BufferRenderer.drawWithGlobalProgram(buffer);
	}

	public void drawStringWithScale(MatrixStack matrixStack, String text, float x, float y, Color color) {
		MinecraftClient mc = MinecraftClient.getInstance();
		matrixStack.push();
		matrixStack.scale(2, 2, 1.0f);
		mc.textRenderer.drawWithShadow(matrixStack, text, x / 2, y / 2, color.getColorAsInt(), false);
		matrixStack.pop();
	}

	public void drawStringWithScale(MatrixStack matrixStack, String text, float x, float y, int color) {
		MinecraftClient mc = MinecraftClient.getInstance();
		matrixStack.push();
		matrixStack.scale(2, 2, 1.0f);
		mc.textRenderer.drawWithShadow(matrixStack, text, x / 2, y / 2, color, false);
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
