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
import net.aoba.module.modules.render.EntityESP.DrawMode;
import net.aoba.rendering.shaders.Shader;
import net.aoba.settings.types.ShaderSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PlayerESP extends Module implements Render3DListener {

	private final EnumSetting<DrawMode> drawMode = EnumSetting.<DrawMode>builder().id("playeresp_draw_mode")
			.displayName("Draw Mode").description("Draw Mode").defaultValue(DrawMode.Model).build();

	private final ShaderSetting color_default = ShaderSetting.builder().id("playeresp_color_default")
			.displayName("Default Color").description("Default Color").defaultValue(Shader.solid(new Color(1f, 1f, 0f))).build();

	private final ShaderSetting color_friendly = ShaderSetting.builder().id("playeresp_color_friendly")
			.displayName("Friendly Color").description("Friendly Color").defaultValue(Shader.solid(new Color(0f, 1f, 0f))).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("playeresp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public PlayerESP() {
		super("PlayerESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see other players with an ESP.");

		addSetting(drawMode);
		addSetting(color_default);
		addSetting(color_friendly);
		addSetting(lineThickness);
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

		for (AbstractClientPlayer entity : MC.level.players()) {
			if (entity == MC.player)
				continue;

			Frustum frustum = event.getRenderer().getFrustum();
			Vec3 cameraPosition = event.getRenderer().getCamera().position();
			if (MC.getEntityRenderDispatcher().shouldRender(entity, frustum, cameraPosition.x(), cameraPosition.y(),
					cameraPosition.z())) {
				Shader effect = getColor(entity);
				switch (drawMode.getValue()) {
				case BoundingBox:
					double interpolatedX = Mth.lerp(partialTicks, entity.xo, entity.getX());
					double interpolatedY = Mth.lerp(partialTicks, entity.yo, entity.getY());
					double interpolatedZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

					AABB boundingBox = entity.getBoundingBox().move(interpolatedX - entity.getX(),
							interpolatedY - entity.getY(), interpolatedZ - entity.getZ());
					event.getRenderer().drawBox(boundingBox, effect, lineThickness.getValue());
					break;
				case Model:
					event.getRenderer().drawEntityModel(entity, effect);
					break;
				}
			}
		}
	}

	private Shader getColor(AbstractClientPlayer entity) {
		if (AOBA_CLIENT.friendsList.contains(entity)) {
			return color_friendly.getValue();
		} else {
			return color_default.getValue();
		}
	}
}
