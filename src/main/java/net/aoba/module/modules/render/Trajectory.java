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
 * Trajectory Module
 */
package net.aoba.module.modules.render;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.aoba.misc.ModuleUtils;
import net.aoba.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;

public class Trajectory extends Module {

	public Trajectory() {
		this.setName("Trajectory");
		this.setBind(new KeyBinding("key.trajectory", GLFW.GLFW_KEY_UNKNOWN, "key.categories.aoba"));
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see where they are aiming. (DISABLED)");
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onUpdate() {
	}

	@Override
	public void onRender(MatrixStack matrixStack, float partialTicks) {
		MinecraftClient mc = MinecraftClient.getInstance();
		
		matrixStack.push();
		
		RenderSystem.setShaderColor(0, 0, 0, 1);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Matrix4f matrix = matrixStack.peek().getPositionMatrix();
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		
		ItemStack itemStack = mc.player.getActiveItem();
		Item item = itemStack.getItem();
		
		if(!(ModuleUtils.isThrowable(itemStack))) return;
		
		float initialVelocity = (52 * BowItem.getPullProgress(mc.player.getItemUseTime()));
		Vec3d prevPoint = new Vec3d(0,0,0);
		
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION);
		for(int iteration = 0; iteration < 1000; iteration++){
			bufferBuilder.vertex(matrix, (float) prevPoint.x, (float) prevPoint.y, (float) prevPoint.z).next();
			
			float distance =  (float) ((initialVelocity)*Math.sin(2*mc.player.getRotationVector().x) / 9.0f);
			Vec3d nextPoint = mc.player.getRotationVector().multiply(distance);
			bufferBuilder.vertex(matrix, (float) nextPoint.x, (float) nextPoint.y, (float) nextPoint.z).next();
			
			prevPoint = nextPoint;
		}
		

		tessellator.draw();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		matrixStack.pop();
	}

	@Override
	public void onSendPacket(Packet<?> packet) {

	}

	@Override
	public void onReceivePacket(Packet<?> packet) {

	}
	
	private double IntepolateThrowPower(Item item) {
		float power = (float) Math.pow(((72000 - MinecraftClient.getInstance().player.getItemUseTime()) / 20.0f), 2.0f);
		
		return power + (power * 2.0f);
	}

}