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
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityESP extends Module implements Render3DListener {
	public enum DrawMode {
		BoundingBox, Model
	}

	private final EnumSetting<DrawMode> drawMode = EnumSetting.<DrawMode>builder().id("entityesp_draw_mode")
			.displayName("Draw Mode").description("Draw Mode").defaultValue(DrawMode.Model).build();

	private final ShaderSetting color_passive = ShaderSetting.builder().id("entityesp_color_passive")
			.displayName("Passive Color").description("Passive Color").defaultValue(Shader.solid(new Color(0f, 1f, 0f, 0.3f)))
			.build();

	private final ShaderSetting color_enemies = ShaderSetting.builder().id("entityesp_color_enemy")
			.displayName("Enemy Color").description("Enemy Color").defaultValue(Shader.solid(new Color(1, 0f, 0f, 0.3f))).build();

	private final ShaderSetting color_misc = ShaderSetting.builder().id("entityesp_color_misc").displayName("Misc. Color")
			.description("Misc. Color").defaultValue(Shader.solid(new Color(0, 0f, 1f, 0.3f))).build();

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
		float partialTicks = event.getRenderer().getDeltaTracker().getGameTimeDeltaPartialTick(true);

		for (Entity entity : Aoba.getInstance().entityManager.getEntities()) {

			Frustum frustum = event.getRenderer().getFrustum();
			Vec3 cameraPosition = event.getRenderer().getCamera().position();
			if (MC.getEntityRenderDispatcher().shouldRender(entity, frustum, cameraPosition.x(),
					cameraPosition.y(), cameraPosition.z())) {
				if (entity instanceof LivingEntity && !(entity instanceof Player)) {

					Shader effect = getColorForEntity(entity);
					if (effect != null) {
						switch (drawMode.getValue()) {
						case BoundingBox:
							double interpolatedX = Mth.lerp(partialTicks, entity.xo, entity.getX());
							double interpolatedY = Mth.lerp(partialTicks, entity.yo, entity.getY());
							double interpolatedZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

							AABB boundingBox = entity.getBoundingBox().move(interpolatedX - entity.getX(),
									interpolatedY - entity.getY(), interpolatedZ - entity.getZ());
							event.getRenderer().drawBox(boundingBox, effect,
									lineThickness.getValue());
							break;
						case Model:
							event.getRenderer().drawEntityModel(entity, effect);
							break;
						}
					}
				}
			}
		}
	}

	private Shader getColorForEntity(Entity entity) {
		if (entity instanceof Animal && showPassiveEntities.getValue()) {
			return color_passive.getValue();
		} else if (entity instanceof Enemy && showEnemies.getValue()) {
			return color_enemies.getValue();
		} else if (!(entity instanceof Animal || entity instanceof Enemy) && showMiscEntities.getValue()) {
			return color_misc.getValue();
		}
		return null;
	}

}
