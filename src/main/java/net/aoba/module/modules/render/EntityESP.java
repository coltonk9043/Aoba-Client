/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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

	private final ColorSetting color_passive = ColorSetting.builder().id("entityesp_color_passive")
			.displayName("Passive Color").description("Passive Color").defaultValue(new Color(0f, 1f, 0f, 0.3f))
			.build();

	private final ColorSetting color_enemies = ColorSetting.builder().id("entityesp_color_enemy")
			.displayName("Enemy Color").description("Enemy Color").defaultValue(new Color(1, 0f, 0f, 0.3f)).build();

	private final ColorSetting color_misc = ColorSetting.builder().id("entityesp_color_misc").displayName("Misc. Color")
			.description("Misc. Color").defaultValue(new Color(0, 0f, 1f, 0.3f)).build();

	private final BooleanSetting showPassiveEntities = BooleanSetting.builder().id("entityesp_show_passive")
			.displayName("Show Passive Entities").description("Show Passive Entities.").defaultValue(true).build();

	private final BooleanSetting showEnemies = BooleanSetting.builder().id("entityesp_show_enemies")
			.displayName("Show Enemies").description("Show Enemies.").defaultValue(true).build();

	private final BooleanSetting showMiscEntities = BooleanSetting.builder().id("entityesp_show_misc")
			.displayName("Show Misc Entities").description("Show Misc Entities").defaultValue(true).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("entityesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public EntityESP() {
		super("EntityESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see entities with an ESP.");

		addSetting(drawMode);
		addSetting(color_passive);
		addSetting(color_enemies);
		addSetting(color_misc);
		addSetting(lineThickness);
		addSetting(showPassiveEntities);
		addSetting(showEnemies);
		addSetting(showMiscEntities);
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
		float partialTicks = event.getRenderTickCounter().getTickProgress(true);

		for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {

			Frustum frustum = event.getFrustum();
			Camera camera = MC.gameRenderer.getCamera();
			Vec3d cameraPosition = camera.getPos();
			if (MC.getEntityRenderDispatcher().shouldRender(entity, frustum, cameraPosition.getX(),
					cameraPosition.getY(), cameraPosition.getZ())) {
				if (entity instanceof LivingEntity && !(entity instanceof PlayerEntity)) {

					Color color = getColorForEntity(entity);
					if (color != null) {
						switch (drawMode.getValue()) {
						case BoundingBox:
							double interpolatedX = MathHelper.lerp(partialTicks, entity.lastX, entity.getX());
							double interpolatedY = MathHelper.lerp(partialTicks, entity.lastY, entity.getY());
							double interpolatedZ = MathHelper.lerp(partialTicks, entity.lastZ, entity.getZ());

							Box boundingBox = entity.getBoundingBox().offset(interpolatedX - entity.getX(),
									interpolatedY - entity.getY(), interpolatedZ - entity.getZ());
							Render3D.draw3DBox(matrixStack, event.getCamera(), boundingBox, color,
									lineThickness.getValue());
							break;
						case Model:
							Render3D.drawEntityModel(matrixStack, camera, partialTicks, entity, color);
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
