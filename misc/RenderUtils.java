package net.aoba.misc;

import net.aoba.gui.Color;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderUtils {
	public void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, float r, float g, float b,
			float alpha) {
		Matrix4f matrix = matrixStack.peek().getModel();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(r, g, b, alpha);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + width, 0).next();
			bufferBuilder.vertex(matrix, x, y + width, 0).next();
			// bufferBuilder.vertex(matrix, x, y, 0).next();
		}
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}
	
	public void drawBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color,
			float alpha) {
		Matrix4f matrix = matrixStack.peek().getModel();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(color.r, color.g, color.b, alpha);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + width, 0).next();
			bufferBuilder.vertex(matrix, x, y + width, 0).next();
			// bufferBuilder.vertex(matrix, x, y, 0).next();
		}
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}
	
	public void drawOutlinedBox(MatrixStack matrixStack, int x, int y, int width, int height, Color color,
			float alpha) {
		Matrix4f matrix = matrixStack.peek().getModel();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(color.r, color.g, color.b, alpha);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + width, 0).next();
			bufferBuilder.vertex(matrix, x, y + width, 0).next();
		}
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		
		RenderSystem.setShaderColor(0, 0, 0, alpha);
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + width, 0).next();
			bufferBuilder.vertex(matrix, x, y + width, 0).next();
			bufferBuilder.vertex(matrix, x, y, 0).next();
		}
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}
	
	public void drawOutline(MatrixStack matrixStack, int x, int y, int width, int height) {
		Matrix4f matrix = matrixStack.peek().getModel();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		
		RenderSystem.setShaderColor(0, 0, 0, 1);
		
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(matrix, x, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y, 0).next();
			bufferBuilder.vertex(matrix, x + width, y + width, 0).next();
			bufferBuilder.vertex(matrix, x, y + width, 0).next();
			bufferBuilder.vertex(matrix, x, y, 0).next();
		}
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	public void draw3DBox(MatrixStack matrixStack, Box box, Color color) {
		Matrix4f matrix = matrixStack.peek().getModel();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(color.r, color.g, color.b, 1.0f);
		
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		{
			bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
			bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();
			
			bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();
			bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();
			
			bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();
			bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();
			
			bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();
			bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
			
			bufferBuilder.vertex(box.minX, box.minY, box.minZ).next();
			bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
			
			bufferBuilder.vertex(box.maxX, box.minY, box.minZ).next();
			bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();
			
			bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).next();
			bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
			
			bufferBuilder.vertex(box.minX, box.minY, box.maxZ).next();
			bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();
			
			bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
			bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();
			
			bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).next();
			bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
			
			bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).next();
			bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();
			
			bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).next();
			bufferBuilder.vertex(box.minX, box.maxY, box.minZ).next();
		}
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}
	
	public void EntityESPBox(Entity entity, float r, float g, float b) {
		float partialRenderTicks = MinecraftClient.getInstance().getTickDelta();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(1F);

		double minX = entity.getBoundingBox().minX + (entity.getPos().x - entity.lastRenderX) * partialRenderTicks;
		double maxX = entity.getBoundingBox().maxX + (entity.getPos().x - entity.lastRenderX) * partialRenderTicks;
		double minY = entity.getBoundingBox().minY + (entity.getPos().y - entity.lastRenderY) * partialRenderTicks;
		double maxY = entity.getBoundingBox().maxY + (entity.getPos().y - entity.lastRenderY) * partialRenderTicks;
		double minZ = entity.getBoundingBox().minZ + (entity.getPos().z - entity.lastRenderZ) * partialRenderTicks;
		double maxZ = entity.getBoundingBox().maxZ + (entity.getPos().z - entity.lastRenderZ) * partialRenderTicks;
		GL11.glColor3f(r, g, b);
		GL11.glBegin(GL11.GL_LINES);
		{

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, minY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glColor4f(r, g, b, 0.15f);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);

		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public void ESPBox(double x, double y, double z, float r, float g, float b) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(1F);
		double minX = x;
		double maxX = x + 0.5f;
		double minY = y;
		double maxY = y + 0.5f;
		double minZ = z;
		double maxZ = z + 0.5f;
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(r, g, b);
		{

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, minY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glColor4f(r, g, b, 0.15f);
			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);

		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void TileEntityESPBox(BlockEntity entity, float r, float g, float b) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(1F);
		double minX = entity.getPos().getX();
		double maxX = entity.getPos().getX() + 1;
		double minY = entity.getPos().getY();
		double maxY = entity.getPos().getY() + 1;
		double minZ = entity.getPos().getZ();
		double maxZ = entity.getPos().getZ() + 1;
		GL11.glColor3f(r, g, b);
		GL11.glBegin(GL11.GL_LINES);
		{

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, minY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);
		}
		GL11.glEnd();

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glColor4f(r, g, b, 0.15f);
			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public void BlockESPBox(BlockPos entity, float r, float g, float b) {
		double minX = entity.getX();
		double maxX = entity.getX() + 1;
		double minY = entity.getY();
		double maxY = entity.getY() + 1;
		double minZ = entity.getZ();
		double maxZ = entity.getZ() + 1;
		GL11.glLineWidth(1F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor3f(r, g, b);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, minY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);
		}
		GL11.glEnd();

		GL11.glColor4f(r, g, b, 0.1f);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);

			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(minX, maxY, maxZ);

			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(minX, minY, minZ);
			GL11.glVertex3d(maxX, minY, minZ);
			GL11.glVertex3d(maxX, minY, maxZ);

			GL11.glVertex3d(minX, minY, maxZ);
			GL11.glVertex3d(maxX, minY, maxZ);
			GL11.glVertex3d(minX, minY, minZ);

			GL11.glVertex3d(minX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, minZ);
			GL11.glVertex3d(maxX, maxY, maxZ);

			GL11.glVertex3d(minX, maxY, maxZ);
			GL11.glVertex3d(maxX, maxY, maxZ);
			GL11.glVertex3d(minX, maxY, minZ);
		}
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

	public void drawLine3D(Entity player, Entity entity) {
		GL11.glPushMatrix();
		float partialRenderTicks = MinecraftClient.getInstance().getTickDelta();
		double x = entity.lastRenderX + (entity.getPos().x - entity.lastRenderX) * partialRenderTicks;
		double y = entity.lastRenderY + (entity.getPos().y - entity.lastRenderY) * partialRenderTicks;
		double z = entity.lastRenderZ + (entity.getPos().z - entity.lastRenderZ) * partialRenderTicks;

		GL11.glBlendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(0f, 0f, 1f);
		{
			GL11.glVertex3d(player.getPos().x, player.getPos().y, player.getPos().z);
			GL11.glVertex3d(x, y, z);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}

	public void drawLine3D(Vec3d pos, Entity entity) {
		GL11.glPushMatrix();
		float partialRenderTicks = MinecraftClient.getInstance().getTickDelta();
		double x = entity.lastRenderX + (entity.getPos().x - entity.lastRenderX) * partialRenderTicks;
		double y = entity.lastRenderY + (entity.getPos().y - entity.lastRenderY) * partialRenderTicks;
		double z = entity.lastRenderZ + (entity.getPos().z - entity.lastRenderZ) * partialRenderTicks;

		GL11.glBlendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(0f, 0f, 1f);
		{
			GL11.glVertex3d(pos.x, pos.y, pos.z);
			GL11.glVertex3d(x, y, z);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}

	public void drawLine3D(Vec3d pos, Vec3d pos2, Color color) {
		GL11.glPushMatrix();
		GL11.glBlendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(color.r / 255f, color.g / 255f, color.b / 255f);
		{
			GL11.glVertex3d(pos.x, pos.y, pos.z);
			GL11.glVertex3d(pos2.x, pos2.y, pos2.z);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}
	
	public static void applyRegionalRenderOffset(MatrixStack matrixStack)
	{
		//applyCameraRotationOnly();
		Vec3d camPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos();
		BlockPos camBlockPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getBlockPos();
		
		int regionX = (camBlockPos.getX() >> 9) * 512;
		int regionZ = (camBlockPos.getZ() >> 9) * 512;
		
		matrixStack.translate(regionX - camPos.x, -camPos.y,
			regionZ - camPos.z);
	}
	
	public static void applyRegionalRenderOffset(MatrixStack matrixStack,
		Chunk chunk)
	{
		//applyCameraRotationOnly();
		Vec3d camPos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos();
		
		int regionX = (chunk.getPos().getStartX() >> 9) * 512;
		int regionZ = (chunk.getPos().getStartZ() >> 9) * 512;
		
		matrixStack.translate(regionX - camPos.x, -camPos.y,
			regionZ - camPos.z);
	}
	
	//public static void applyCameraRotationOnly()
	//{
	//	Camera camera = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera;
	//	GL11.glRotated(MathHelper.wrapDegrees(camera.getPitch()), 1, 0, 0);
	//	GL11.glRotated(MathHelper.wrapDegrees(camera.getYaw() + 180.0), 0, 1, 0);
	//}
}
