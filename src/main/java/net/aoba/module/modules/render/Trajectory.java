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
 * Trajectory Module
 */
package net.aoba.module.modules.render;

import java.util.function.Predicate;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;
import net.aoba.Aoba;
import net.aoba.event.events.RenderEvent;
import net.aoba.event.listeners.RenderListener;
import net.aoba.gui.colors.Color;
import net.aoba.misc.ModuleUtils;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;

public class Trajectory extends Module implements RenderListener {

	private ColorSetting color = new ColorSetting("trajectory_color", "Color", "Color", new Color(0, 1f, 1f));
	private FloatSetting blipSize = new FloatSetting("trajectory_blipsize", "Blip Size", "Blip Size", 0.15f, 0.05f, 1.0f, 0.05f);
	
	public Trajectory() {
		super(new KeybindSetting("key.trajectory", "Trajectory Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("Trajectory");
		this.setCategory(Category.Render);
		this.setDescription("Allows the player to see where they are aiming. (DISABLED)");
		
		this.addSetting(color);
		this.addSetting(blipSize);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(RenderListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(RenderListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	@Override
	public void OnRender(RenderEvent event) {
		if(MC.player.isUsingItem()) {
			Color renderColor = color.getValue();
			Matrix4f matrix = event.GetMatrix().peek().getPositionMatrix();
			
			ItemStack itemStack = MC.player.getActiveItem();
			if(ModuleUtils.isThrowable(itemStack)) {
				float initialVelocity = (52f * BowItem.getPullProgress(MC.player.getItemUseTime()));
				
				Camera camera = MC.gameRenderer.getCamera();
				Vec3d offset = RenderUtils.getEntityPositionOffsetInterpolated(MC.cameraEntity, event.GetPartialTicks());
				Vec3d eyePos = MC.cameraEntity.getEyePos();
				
				// Calculate look direction.
				Vec3d right = Vec3d.fromPolar(0, camera.getYaw() + 90).multiply(0.14f);
				Vec3d lookDirection = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());
				Vec3d velocity = lookDirection.multiply(initialVelocity).multiply(0.2f);
				
				// Calculate starting point.
				Vec3d prevPoint = new Vec3d(0, 0, 0).add(eyePos).subtract(offset).add(right);
				Vec3d landPosition = null;
				
				RenderSystem.setShaderColor(renderColor.getRedFloat(), renderColor.getGreenFloat(), renderColor.getBlueFloat(), renderColor.getAlphaFloat());
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				
				Tessellator tessellator = RenderSystem.renderThreadTesselator();
				RenderSystem.setShader(GameRenderer::getPositionProgram);
				BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);

				for(int iteration = 0; iteration < 150; iteration++){
					Vec3d nextPoint = prevPoint.add(velocity.multiply(0.1));
					bufferBuilder.vertex(matrix, (float) prevPoint.x, (float) prevPoint.y, (float) prevPoint.z);

					// Check to see if we have collided with a block.
					RaycastContext context = new RaycastContext(prevPoint, nextPoint, RaycastContext.ShapeType.COLLIDER, FluidHandling.NONE, MC.player);
					BlockHitResult result = MC.world.raycast(context);
					if(result.getType() != HitResult.Type.MISS) {
						// Arrow is collided with a block, draw one last vertice and set land position to the raycast result position.
						landPosition = result.getPos();
						bufferBuilder.vertex(matrix, (float) landPosition.x, (float) landPosition.y, (float) landPosition.z);
						break;
					}else {
						// We did NOT find a collision with a block, check entities.
						Box box = new Box(prevPoint, nextPoint);
						Predicate<Entity> predicate = e -> !e.isSpectator() && e.canHit();
						EntityHitResult entityResult = ProjectileUtil.raycast(MC.player, prevPoint, nextPoint, box, predicate, 4096);
						
						if(entityResult != null && entityResult.getType() != HitResult.Type.MISS) {
							// Arrow is collided with an entity, draw one last vertice and set land position to the raycast result position.
							landPosition = entityResult.getPos();
							bufferBuilder.vertex(matrix, (float) landPosition.x, (float) landPosition.y, (float) landPosition.z);
							break;
						}else {
							// No collisions from raycast, draw next vertice.
							bufferBuilder.vertex(matrix, (float) nextPoint.x, (float) nextPoint.y, (float) nextPoint.z);
						}
					}

					prevPoint = nextPoint;
					velocity = velocity.multiply(0.99).add(0, -0.045f, 0);
				}
				

				BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
				RenderSystem.setShaderColor(1, 1, 1, 1);
				
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				
				// Draw Cube if a landing position exists.
				if(landPosition != null) {
					float size = blipSize.getValue();
					Vec3d pos1 = landPosition.add(-size, -size, -size);
					Vec3d pos2 = landPosition.add(size, size, size);
					Box box = new Box(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
					RenderUtils.draw3DBox(matrix, box, renderColor);
				}
			}
		}
	}
}