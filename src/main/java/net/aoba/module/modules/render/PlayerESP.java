/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.module.modules.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.aoba.Aoba;
import net.aoba.event.events.Render3DEvent;
import net.aoba.event.listeners.Render3DListener;
import net.aoba.gui.colors.Color;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.module.modules.render.EntityESP.DrawMode;
import net.aoba.settings.types.ColorSetting;
import net.aoba.settings.types.EnumSetting;
import net.aoba.settings.types.FloatSetting;
import net.aoba.utils.render.Render3D;
import net.minecraft.client.Camera;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PlayerESP extends Module implements Render3DListener {

	private final EnumSetting<DrawMode> drawMode = EnumSetting.<DrawMode>builder().id("playeresp_draw_mode")
			.displayName("Draw Mode").description("Draw Mode").defaultValue(DrawMode.Model).build();

	private final ColorSetting color_default = ColorSetting.builder().id("playeresp_color_default")
			.displayName("Default Color").description("Default Color").defaultValue(new Color(1f, 1f, 0f)).build();

	private final ColorSetting color_friendly = ColorSetting.builder().id("playeresp_color_friendly")
			.displayName("Friendly Color").description("Friendly Color").defaultValue(new Color(0f, 1f, 0f)).build();

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
		PoseStack matrixStack = event.GetMatrix();
		float partialTicks = event.getRenderTickCounter().getGameTimeDeltaPartialTick(true);

		for (AbstractClientPlayer entity : MC.level.players()) {
			if (entity == MC.player)
				continue;

			Frustum frustum = event.getFrustum();
			Camera camera = MC.gameRenderer.getMainCamera();
			Vec3 cameraPosition = camera.position();
			if (MC.getEntityRenderDispatcher().shouldRender(entity, frustum, cameraPosition.x(), cameraPosition.y(),
					cameraPosition.z())) {
				Color color = getColor(entity);
				switch (drawMode.getValue()) {
				case BoundingBox:
					double interpolatedX = Mth.lerp(partialTicks, entity.xo, entity.getX());
					double interpolatedY = Mth.lerp(partialTicks, entity.yo, entity.getY());
					double interpolatedZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

					AABB boundingBox = entity.getBoundingBox().move(interpolatedX - entity.getX(),
							interpolatedY - entity.getY(), interpolatedZ - entity.getZ());
					Render3D.draw3DBox(matrixStack, event.getCamera(), boundingBox, color, lineThickness.getValue());
					break;
				case Model:
					Render3D.drawEntityModel(matrixStack, camera, partialTicks, entity, color);
					break;
				}
			}
		}
	}

	private Color getColor(AbstractClientPlayer entity) {
		if (AOBA_CLIENT.friendsList.contains(entity)) {
			return color_friendly.getValue();
		} else {
			return color_default.getValue();
		}
	}
}
