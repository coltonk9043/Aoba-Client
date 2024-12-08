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
 * EntityESP Module
 */
package net.aoba.module.modules.render;

import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityESP extends Module implements Render3DListener {
	public enum DrawMode {
		BoundingBox, Model
	}

	private final EnumSetting<DrawMode> drawMode = EnumSetting.<DrawMode>builder().id("entityesp_draw_mode")
			.displayName("Draw Mode").description("Draw Mode").defaultValue(DrawMode.Model).build();

	private ColorSetting color_passive = ColorSetting.builder().id("entityesp_color_passive")
			.displayName("Passive Color").description("Passive Color").defaultValue(new Color(0f, 1f, 0f, 0.3f))
			.build();

	private ColorSetting color_enemies = ColorSetting.builder().id("entityesp_color_enemy").displayName("Enemy Color")
			.description("Enemy Color").defaultValue(new Color(1, 0f, 0f, 0.3f)).build();

	private ColorSetting color_misc = ColorSetting.builder().id("entityesp_color_misc").displayName("Misc. Color")
			.description("Misc. Color").defaultValue(new Color(0, 0f, 1f, 0.3f)).build();

	private BooleanSetting showPassiveEntities = BooleanSetting.builder().id("entityesp_show_passive")
			.displayName("Show Passive Entities").description("Show Passive Entities.").defaultValue(true).build();

	private BooleanSetting showEnemies = BooleanSetting.builder().id("entityesp_show_enemies")
			.displayName("Show Enemies").description("Show Enemies.").defaultValue(true).build();

	private BooleanSetting showMiscEntities = BooleanSetting.builder().id("entityesp_show_misc")
			.displayName("Show Misc Entities").description("Show Misc Entities").defaultValue(true).build();

	private FloatSetting lineThickness = FloatSetting.builder().id("entityesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public EntityESP() {
		super("EntityESP");
		this.setCategory(Category.of("Render"));
		this.setDescription("Allows the player to see entities with an ESP.");

		this.addSetting(drawMode);
		this.addSetting(color_passive);
		this.addSetting(color_enemies);
		this.addSetting(color_misc);
		this.addSetting(lineThickness);
		this.addSetting(showPassiveEntities);
		this.addSetting(showEnemies);
		this.addSetting(showMiscEntities);
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
	public void onRender(Render3DEvent event) {
		MatrixStack matrixStack = event.GetMatrix();
		float partialTicks = event.getRenderTickCounter().getTickDelta(true);

		for (Entity entity : MC.world.getEntities()) {

			Frustum frustum = event.getFrustum();
			Camera camera = MC.gameRenderer.getCamera();
			Vec3d cameraPosition = camera.getPos();
			if (MC.getEntityRenderDispatcher().shouldRender(entity, frustum, cameraPosition.getX(),
					cameraPosition.getY(), cameraPosition.getZ())) {
				if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {

					Color color = getColorForEntity(entity);
					if (color != null) {
						switch (drawMode.getValue()) {
						case DrawMode.BoundingBox:
							double interpolatedX = MathHelper.lerp(partialTicks, entity.prevX, entity.getX());
							double interpolatedY = MathHelper.lerp(partialTicks, entity.prevY, entity.getY());
							double interpolatedZ = MathHelper.lerp(partialTicks, entity.prevZ, entity.getZ());

							Box boundingBox = entity.getBoundingBox().offset(interpolatedX - entity.getX(),
									interpolatedY - entity.getY(), interpolatedZ - entity.getZ());
							Render3D.draw3DBox(matrixStack, boundingBox, color, lineThickness.getValue());
							break;
						case DrawMode.Model:
							Render3D.drawEntityModel(matrixStack, partialTicks, entity, color,
									lineThickness.getValue());
							break;
						}
					}
				}
			}
		}
	}

	private Color getColorForEntity(Entity entity) {
		if (entity instanceof AnimalEntity && showPassiveEntities.getValue()) {
			return color_passive.getValue();
		} else if (entity instanceof Monster && showEnemies.getValue()) {
			return color_enemies.getValue();
		} else if (!(entity instanceof AnimalEntity || entity instanceof Monster) && showMiscEntities.getValue()) {
			return color_misc.getValue();
		}
		return null;
	}

}
