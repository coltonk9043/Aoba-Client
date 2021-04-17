package aoba.main.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import aoba.main.gui.Color;

public class RenderUtils {
	ActiveRenderInfo activeRender;

	public RenderUtils() {
		activeRender = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
	}

	public void applyRenderOffset() {
		applyCameraRotationOnly();
		Vector3d renderOffset = activeRender.getProjectedView();
		GL11.glTranslated(-renderOffset.x, -renderOffset.y, -renderOffset.z);
	}

	public void applyCameraRotationOnly() {
		GL11.glRotated(MathHelper.wrapDegrees(activeRender.getPitch()), 1, 0, 0);
		GL11.glRotated(MathHelper.wrapDegrees(activeRender.getYaw() + 180.0), 0, 1, 0);
	}

	public void drawBox(int x, int y, int width, int height, float r, float g, float b, float alpha) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		GL11.glColor4f(r, g, b, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x, y + height);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x + width, y);
		}
		GL11.glEnd();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}
	
	public void drawGradientBox(int x, int y, int width, int height, float r, float g, float b, float alpha) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glColor4f(1f, 1f, 1f, alpha);
			GL11.glVertex2i(x, y);
			GL11.glColor4f(0f, 0f, 0f, alpha);
			GL11.glVertex2i(x, y + height);
			GL11.glColor4f(r, g, b, alpha);
			GL11.glVertex2i(x + width, y + height);
			GL11.glColor4f(0f, 0f, 0f, alpha);
			GL11.glVertex2i(x + width, y);
		}
		GL11.glEnd();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}
	
	public void drawBox(int x, int y, int width, int height, float r, float g, float b) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		GL11.glColor3f(r, g, b);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x, y + height);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x + width, y);
		}
		GL11.glEnd();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}
	
	public void drawBox(int x, int y, int width, int height, Color color, float alpha) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		float r = color.r;
		float g = color.g;
		float b = color.b;
		GL11.glColor4f(r / 255, g / 255, b / 255, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x, y + height);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x + width, y);
		}
		GL11.glEnd();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	public void drawOutlinedBox(int x, int y, int width, int height, float r, float g, float b, float alpha) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		GL11.glColor4f(r, g, b, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x, y + height);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x + width, y);
		}
		GL11.glEnd();
		GL11.glLineWidth(1.0F);
		GL11.glColor4f(0f, 0f, 0f, 1f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{

			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + width, y);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x, y + height);
		}
		GL11.glEnd();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}
	
	public void drawOutlinedBox(int x, int y, int width, int height, Color color, float alpha) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		float r = color.r;
		float g = color.g;
		float b = color.b;
		GL11.glColor4f(r / 255, g / 255, b / 255, alpha);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x, y + height);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x + width, y);
		}
		GL11.glEnd();
		GL11.glLineWidth(1.0F);
		GL11.glColor4f(0f, 0f, 0f, 1f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{

			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + width, y);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x, y + height);
		}
		GL11.glEnd();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	public void drawOutline(int x, int y, int width, int height) {
		RenderSystem.disableTexture();
		RenderSystem.disableDepthTest();
		GL11.glLineWidth(1.0F);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glColor4f(0f, 0f, 0f, 1f);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + width, y);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x, y + height);
		}
		GL11.glEnd();
		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
	}

	public void EntityESPBox(Entity entity, float r, float g, float b) {
		float partialRenderTicks = Minecraft.getInstance().getRenderPartialTicks();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(1F);
		double minX = entity.getBoundingBox().minX + (entity.getPosX() - entity.lastTickPosX) * partialRenderTicks;
		double maxX = entity.getBoundingBox().maxX + (entity.getPosX() - entity.lastTickPosX) * partialRenderTicks;
		double minY = entity.getBoundingBox().minY + (entity.getPosY() - entity.lastTickPosY) * partialRenderTicks;
		double maxY = entity.getBoundingBox().maxY + (entity.getPosY() - entity.lastTickPosY) * partialRenderTicks;
		double minZ = entity.getBoundingBox().minZ + (entity.getPosZ() - entity.lastTickPosZ) * partialRenderTicks;
		double maxZ = entity.getBoundingBox().maxZ + (entity.getPosZ() - entity.lastTickPosZ) * partialRenderTicks;
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

	public void TileEntityESPBox(TileEntity entity, float r, float g, float b) {
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
		float partialRenderTicks = Minecraft.getInstance().getRenderPartialTicks();
		double x = entity.prevPosX
				+ (entity.getPosX() - entity.prevPosX) * partialRenderTicks;
		double y = entity.prevPosY
				+ (entity.getPosY() - entity.prevPosY) * partialRenderTicks;
		double z = entity.prevPosZ
				+ (entity.getPosZ() - entity.prevPosZ) * partialRenderTicks;

		GL11.glBlendFunc(770, 771);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(0f, 0f, 1f);
		{
			GL11.glVertex3d(player.getPosX(), player.getPosY(), player.getPosZ());
			GL11.glVertex3d(x, y, z);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}
	
	public void drawLine3D(Vector3d pos, Entity entity) {
		GL11.glPushMatrix();
		float partialRenderTicks = Minecraft.getInstance().getRenderPartialTicks();
		double x = entity.prevPosX
				+ (entity.getPosX() - entity.prevPosX) * partialRenderTicks;
		double y = entity.prevPosY
				+ (entity.getPosY() - entity.prevPosY) * partialRenderTicks;
		double z = entity.prevPosZ
				+ (entity.getPosZ() - entity.prevPosZ) * partialRenderTicks;

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

	public void drawLine3D(Vector3d pos, Vector3d pos2, Color color) {
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
}
