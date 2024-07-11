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
 * Tracer Module
 */
package net.aoba.module.modules.render;

import org.lwjgl.glfw.GLFW;
import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.gui.colors.Color;
import net.aoba.misc.RenderUtils;
import net.aoba.module.Module;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.KeybindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.Vec3d;

public class Tracer extends Module implements Render3DListener {
	private ColorSetting color_player = new ColorSetting("tracer_color_player", "Player Color",  "Player Color", new Color(1f, 1f, 0f));
	private ColorSetting color_passive = new ColorSetting("tracer_color_passive", "Passive Color",  "Passive Color", new Color(0, 1f, 1f));
	private ColorSetting color_enemies = new ColorSetting("tracer_color_enemy", "Enemy Color", "Enemy Color", new Color(0, 1f, 1f));
	private ColorSetting color_misc = new ColorSetting("tracer_color_misc", "Misc. Color", "Misc. Color", new Color(0, 1f, 1f));
	
	public Tracer() {
		super(new KeybindSetting("key.tracer", "Tracer Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));
		
		this.setName("Tracer");
		this.setCategory(Category.Render);
		this.setDescription("Points toward other players and entities with a line.");
		
		this.addSetting(color_player);
		this.addSetting(color_passive);
		this.addSetting(color_enemies);
		this.addSetting(color_misc);
	}

	@Override
	public void onDisable() {
		Aoba.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
	}

	@Override
	public void onEnable() {
		Aoba.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	@Override
	public void onToggle() {

	}
	
	@Override
	public void OnRender(Render3DEvent event) {
		Vec3d eyePosition = new Vec3d(0, 0, 1);
		Camera camera = MC.gameRenderer.getCamera();
		Vec3d offset = RenderUtils.getEntityPositionOffsetInterpolated(MC.cameraEntity, event.GetPartialTicks());

		eyePosition = eyePosition.rotateX((float) -Math.toRadians(camera.getPitch()));
		eyePosition = eyePosition.rotateY((float) -Math.toRadians(camera.getYaw()));
		eyePosition = eyePosition.add(MC.cameraEntity.getEyePos());
		eyePosition = eyePosition.subtract(offset);
		
		float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
		for (Entity entity : MC.world.getEntities()) {
			if(entity instanceof LivingEntity && (entity != MC.player)) {
				Vec3d interpolated = RenderUtils.getEntityPositionInterpolated(entity, tickDelta);
				if (entity instanceof AnimalEntity) {
					RenderUtils.drawLine3D(event.GetMatrix(), eyePosition, interpolated, color_passive.getValue(), 1.0f);
				} else if (entity instanceof Monster) {
					RenderUtils.drawLine3D(event.GetMatrix(), eyePosition, interpolated, color_enemies.getValue(), 1.0f);
				} else {
					RenderUtils.drawLine3D(event.GetMatrix(), eyePosition, interpolated, color_misc.getValue(), 1.0f);
				}
			}
		}
		
		for(AbstractClientPlayerEntity player : MC.world.getPlayers()) {
			Vec3d interpolated = RenderUtils.getEntityPositionInterpolated(player, tickDelta);
			RenderUtils.drawLine3D(event.GetMatrix(), eyePosition, interpolated, color_player.getValue(), 1.0f);
		}
	}
}
